package com.powerup.model.loan.gateways;

import com.powerup.model.loan.Loan;
import reactor.core.publisher.Mono;

public interface ILoanRepositoryPort {
    Mono<Loan> saveLoan(Loan loan);
}
