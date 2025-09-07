package com.powerup.usecase.loan;

import com.powerup.enums.ExceptionMessages;
import com.powerup.enums.LoanStatus;
import com.powerup.exception.ForbiddenException;
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
import com.powerup.port.token.ISecurityContextPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

import static com.powerup.usecase.util.LoanUtils.buildLoanWithUserData;


@RequiredArgsConstructor
public class LoanUseCase {

    private final ILoanRepositoryPort loanRepositoryPort;
    private final ILoanTypeRepositoryPort loanTypeRepositoryPort;
    private final ILoanStateRepositoryPort loanStateRepositoryPort;
    private final IUserConsumerPort userConsumerPort;
    private final ISecurityContextPort securityContextPort;

    public Mono<Loan> saveLoan(Loan loan) {
        return securityContextPort.getUserEmail()
                .flatMap(tokenEmail ->
                        userConsumerPort.getUserByIdentityDocument(loan.getIdentityDocument())
                                .filter(user -> user.getEmail().equalsIgnoreCase(tokenEmail))
                                .switchIfEmpty(Mono.error(new ForbiddenException(ExceptionMessages.FORBIDDEN_LOAN_CREATION.getMessage())))
                                .flatMap(user -> validateLoanType(loan.getIdLoanType())
                                        .then(Mono.just(buildLoanWithUserData(loan, user)))
                                )
                                .flatMap(loanRepositoryPort::saveLoan)
                );
    }

    private Mono<Void> validateLoanType(Long loanTypeId) {
        return loanTypeRepositoryPort.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(ExceptionMessages.LOAN_TYPE_NOT_FOUND.format(loanTypeId))))
                .then();
    }

    public Flux<LoanForReview> getLoansForReviewByStatus(String status, int page, int size) {

        Flux<Long> stateIdsFlux = Mono.justOrEmpty(status)
                .flatMapMany(s -> loanStateRepositoryPort.findByName(s).map(LoanState::getId))
                .switchIfEmpty(loanStateRepositoryPort.findByNameNot(LoanStatus.APPROVED.getStatus()).map(LoanState::getId));

        return stateIdsFlux
                .flatMap(stateId -> loanRepositoryPort.findLoansForReview(stateId, page, size))
                .take(size)
                .flatMap(loan -> {
                    Mono<LoanState> loanstateMono = loanStateRepositoryPort.findById(loan.getIdLoanState());
                    Mono<LoanType> loanTypeMono = loanTypeRepositoryPort.findById(loan.getIdLoanType());
                    Mono<UserConsumer> userMono = userConsumerPort.getUserByEmail(loan.getEmail());
                    Mono<BigDecimal> totalDebtMono = loanRepositoryPort.findLoansForReviewApprovedByEmail(loan.getEmail())
                            .map(Loan::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return Mono.zip(loanstateMono, loanTypeMono, userMono, totalDebtMono)
                            .map(tuple -> {
                                LoanState state = tuple.getT1();
                                LoanType type = tuple.getT2();
                                UserConsumer user = tuple.getT3();
                                BigDecimal totalDebt = tuple.getT4();

                                return LoanForReview.builder()
                                        .amount(loan.getAmount())
                                        .term(loan.getTerm())
                                        .email(loan.getEmail())
                                        .loanStatus(state.getName())
                                        .loanType(type.getName())
                                        .interestRate(type.getInterestRate())
                                        .clientName(user.getFirstName() + " " + user.getLastName())
                                        .baseSalary(user.getBaseSalary())
                                        .totalMonthlyDebtApprovedLoans(totalDebt)
                                        .build();
                            });
                });
    }
}
