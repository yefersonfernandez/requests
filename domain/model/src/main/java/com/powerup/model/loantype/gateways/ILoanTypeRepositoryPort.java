package com.powerup.model.loantype.gateways;

import com.powerup.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface ILoanTypeRepositoryPort {
    Mono<LoanType> findById(Long id);
}
