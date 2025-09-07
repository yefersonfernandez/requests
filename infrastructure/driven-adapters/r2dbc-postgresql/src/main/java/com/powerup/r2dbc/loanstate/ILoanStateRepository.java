package com.powerup.r2dbc.loanstate;

import com.powerup.r2dbc.entity.LoanStateEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ILoanStateRepository extends ReactiveCrudRepository<LoanStateEntity, Long>, ReactiveQueryByExampleExecutor<LoanStateEntity> {
    Mono<LoanStateEntity> findByName(String name);
    Flux<LoanStateEntity> findByNameNot(String name);
}
