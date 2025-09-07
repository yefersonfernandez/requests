package com.powerup.r2dbc.loan;

import com.powerup.r2dbc.entity.LoanEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ILoanRepository extends ReactiveCrudRepository<LoanEntity, Long>, ReactiveQueryByExampleExecutor<LoanEntity> {
    Flux<LoanEntity> findByIdLoanState(Long state, Pageable pageable);
    Flux<LoanEntity> findAllByEmailAndIdLoanState(String email, Long idLoanState);
}
