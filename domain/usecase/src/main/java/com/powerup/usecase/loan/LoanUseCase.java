package com.powerup.usecase.loan;

import com.powerup.enums.ExceptionMessages;
import com.powerup.exception.ForbiddenException;
import com.powerup.exception.LoanTypeNotFoundException;
import com.powerup.model.loan.Loan;
import com.powerup.model.loan.gateways.ILoanRepositoryPort;
import com.powerup.model.loantype.gateways.ILoanTypeRepositoryPort;
import com.powerup.port.consumer.IUserConsumerPort;
import com.powerup.port.consumer.model.UserConsumer;
import com.powerup.port.token.ISecurityContextPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanUseCase {

    private final ILoanRepositoryPort loanRepositoryPort;
    private final ILoanTypeRepositoryPort loanTypeRepositoryPort;
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

    private Loan buildLoanWithUserData(Loan loan, UserConsumer userConsumer) {
        return Loan.builder()
                .amount(loan.getAmount())
                .term(loan.getTerm())
                .email(userConsumer.getEmail())
                .idLoanType(loan.getIdLoanType())
                .idLoanState(1L)
                .build();
    }

    private Mono<Void> validateLoanType(Long loanTypeId) {
        return loanTypeRepositoryPort.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(ExceptionMessages.LOAN_TYPE_NOT_FOUND.format(loanTypeId))))
                .then();
    }
}
