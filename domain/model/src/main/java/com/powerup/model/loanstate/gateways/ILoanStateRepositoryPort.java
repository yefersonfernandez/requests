package com.powerup.model.loanstate.gateways;

import com.powerup.model.loanstate.LoanState;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ILoanStateRepositoryPort {
    Mono<LoanState> findById(Long id);
    Mono<LoanState> findByName(String name);
    Flux<LoanState> findByNameNot(String name);
}
