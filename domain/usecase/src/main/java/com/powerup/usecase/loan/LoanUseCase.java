package com.powerup.usecase.loan;

import com.powerup.enums.ExceptionMessages;
import com.powerup.exception.LoanTypeNotFoundException;
import com.powerup.exception.UserNotFoundException;
import com.powerup.model.loan.Loan;
import com.powerup.model.loan.gateways.ILoanRepositoryPort;
import com.powerup.model.loantype.gateways.ILoanTypeRepositoryPort;
import com.powerup.port.consumer.IUserConsumerPort;
import com.powerup.port.consumer.model.UserConsumer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanUseCase {

    private final ILoanRepositoryPort loanRepositoryPort;
    private final ILoanTypeRepositoryPort loanTypeRepositoryPort;
    private final IUserConsumerPort userConsumerPort;

    public Mono<Loan> saveLoan(Loan loan) {
        return userConsumerPort.getUserByIdentityDocument(loan.getIdentityDocument())
                .switchIfEmpty(Mono.error(new UserNotFoundException(
                        ExceptionMessages.USER_NOT_FOUND.format(loan.getIdentityDocument())
                )))
                .flatMap(userConsumer -> validateLoanType(loan.getIdLoanType())
                        .then(Mono.just(buildLoanWithUserData(loan, userConsumer)))
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
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(
                        ExceptionMessages.LOAN_TYPE_NOT_FOUND.format(loanTypeId)
                )))
                .then();
    }
}
