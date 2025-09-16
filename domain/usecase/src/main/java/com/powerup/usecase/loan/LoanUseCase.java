package com.powerup.usecase.loan;

import com.powerup.enums.ExceptionMessages;
import com.powerup.enums.LoanStatus;
import com.powerup.exception.ForbiddenException;
import com.powerup.exception.LoanNotFoundException;
import com.powerup.exception.LoanStateNotFoundException;
import com.powerup.exception.LoanTypeNotFoundException;
import com.powerup.model.LoanForReview;
import com.powerup.model.loan.Loan;
import com.powerup.model.loan.gateways.ILoanRepositoryPort;
import com.powerup.model.loanstate.LoanState;
import com.powerup.model.loanstate.gateways.ILoanStateRepositoryPort;
import com.powerup.model.loantype.LoanType;
import com.powerup.model.loantype.gateways.ILoanTypeRepositoryPort;
import com.powerup.port.consumer.IUserConsumerPort;
import com.powerup.port.consumer.model.UserConsumer;
import com.powerup.port.sqs.ISqsSenderPort;
import com.powerup.port.sqs.model.CapacityValidationMessage;
import com.powerup.port.token.ISecurityContextPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

import static com.powerup.usecase.util.LoanUtils.buildActiveLoanInfo;
import static com.powerup.usecase.util.LoanUtils.buildCapacityValidationMessage;
import static com.powerup.usecase.util.LoanUtils.buildLoanApprovedMessage;
import static com.powerup.usecase.util.LoanUtils.buildLoanDecisionMessage;
import static com.powerup.usecase.util.LoanUtils.buildLoanForReview;
import static com.powerup.usecase.util.LoanUtils.buildLoanWithUserData;
import static com.powerup.usecase.util.LoanUtils.calculateMonthlyInstallment;


@RequiredArgsConstructor
public class LoanUseCase {

    private final ILoanRepositoryPort loanRepositoryPort;
    private final ILoanTypeRepositoryPort loanTypeRepositoryPort;
    private final ILoanStateRepositoryPort loanStateRepositoryPort;
    private final IUserConsumerPort userConsumerPort;
    private final ISecurityContextPort securityContextPort;
    private final ISqsSenderPort sqsSenderPort;

    public Mono<Loan> saveLoan(Loan loan) {
        return validateUserEmail(loan)
                .flatMap(user -> getLoanTypeOrError(loan)
                        .flatMap(loanType -> {
                            Loan loanToSave = buildLoanWithUserData(loan, user);
                            return loanRepositoryPort.saveLoan(loanToSave)
                                    .flatMap(savedLoan -> handleAutomaticValidation(savedLoan, user, loanType));
                        })
                );
    }

    public Flux<LoanForReview> getLoansForReviewByStatus(String status, int page, int size) {
        return Mono.justOrEmpty(status)
                .flatMapMany(s -> loanStateRepositoryPort.findByName(s).map(LoanState::getId))
                .switchIfEmpty(loanStateRepositoryPort.findByNameNot(LoanStatus.APPROVED.getStatus()).map(LoanState::getId))
                .flatMap(loanStateId -> loanRepositoryPort.findLoansForReview(loanStateId, page, size))
                .take(size)
                .flatMap(loan -> {
                    Mono<LoanState> loanStateMono = loanStateRepositoryPort.findById(loan.getIdLoanState());
                    Mono<LoanType> loanTypeMono = loanTypeRepositoryPort.findById(loan.getIdLoanType());
                    Mono<UserConsumer> userMono = userConsumerPort.getUserByEmail(loan.getEmail());
                    return Mono.zip(loanStateMono, loanTypeMono, userMono)
                            .flatMap(tuple -> {
                                LoanState loanState = tuple.getT1();
                                LoanType loanType = tuple.getT2();
                                UserConsumer user = tuple.getT3();
                                return loanRepositoryPort.findLoansForReviewApprovedByEmail(loan.getEmail())
                                        .flatMap(approvedLoan -> calculateMonthlyInstallment(approvedLoan.getAmount(), approvedLoan.getTerm(), loanType.getInterestRate()))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                                        .map(totalMonthlyDebt -> buildLoanForReview(loan, user, loanType, loanState, totalMonthlyDebt));
                            });
                });
    }

    public Mono<Loan> processLoanDecision(Long id, String decision) {
        return updateLoanStateInternal(id, decision)
                .flatMap(savedLoan -> sqsSenderPort.sendMessage(buildLoanDecisionMessage(savedLoan, decision))
                        .thenReturn(savedLoan)
                );
    }

    public Mono<Loan> updateLoanState(Long id, String decision) {
        return updateLoanStateInternal(id, decision);
    }

    private Mono<UserConsumer> validateUserEmail(Loan loan) {
        return securityContextPort.getUserEmail()
                .flatMap(tokenEmail ->
                        userConsumerPort.getUserByIdentityDocument(loan.getIdentityDocument())
                                .filter(user -> user.getEmail().equalsIgnoreCase(tokenEmail))
                                .switchIfEmpty(Mono.error(new ForbiddenException(ExceptionMessages.FORBIDDEN_LOAN_CREATION.getMessage())))
                );
    }

    private Mono<LoanType> getLoanTypeOrError(Loan loan) {
        return loanTypeRepositoryPort.findById(loan.getIdLoanType())
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(ExceptionMessages.LOAN_TYPE_NOT_FOUND.format(loan.getIdLoanType()))));
    }

    private Mono<Loan> handleAutomaticValidation(Loan loan, UserConsumer user, LoanType loanType) {
        return Mono.justOrEmpty(loanType.getAutomaticValidation())
                .filter(Boolean::booleanValue)
                .flatMap(v -> prepareCapacityValidationMessage(loan, user, loanType)
                        .flatMap(sqsSenderPort::sendCapacityValidationMessage)
                )
                .then(Mono.just(loan));
    }

    private Mono<CapacityValidationMessage> prepareCapacityValidationMessage(
            Loan loan, UserConsumer user, LoanType currentLoanType) {

        return loanRepositoryPort.findLoansForReviewApprovedByEmail(user.getEmail())
                .flatMap(activeLoan -> loanTypeRepositoryPort.findById(activeLoan.getIdLoanType())
                        .map(activeLoanType -> buildActiveLoanInfo(activeLoan, activeLoanType))
                )
                .collectList()
                .map(activeLoans -> buildCapacityValidationMessage(loan, user, currentLoanType, activeLoans));
    }

    private Mono<Loan> updateLoanStateInternal(Long id, String decision) {
        return loanRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new LoanNotFoundException(ExceptionMessages.LOAN_NOT_FOUND.format(id))))
                .flatMap(loan -> loanStateRepositoryPort.findByName(decision)
                        .switchIfEmpty(Mono.error(new LoanStateNotFoundException(ExceptionMessages.LOAN_STATE_NOT_FOUND.format(decision))))
                        .map(loanState -> { loan.setIdLoanState(loanState.getId()); return loan; })
                )
                .flatMap(loanRepositoryPort::saveLoan)
                .flatMap(savedLoan ->
                        LoanStatus.APPROVED.getStatus().equals(decision)
                                ? sqsSenderPort.sendLoanApprovedMessage(buildLoanApprovedMessage(savedLoan)).thenReturn(savedLoan)
                                : Mono.just(savedLoan)
                );
    }
}
