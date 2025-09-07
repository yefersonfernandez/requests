package com.powerup.r2dbc.loanstate;

import com.powerup.model.loanstate.LoanState;
import com.powerup.model.loanstate.gateways.ILoanStateRepositoryPort;
import com.powerup.r2dbc.entity.LoanStateEntity;
import com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class LoanStateRepositoryAdapter extends ReactiveAdapterOperations<LoanState, LoanStateEntity, Long, ILoanStateRepository>  implements ILoanStateRepositoryPort {
    public LoanStateRepositoryAdapter(ILoanStateRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, LoanState.class), transactionalOperator);
    }

    @Override
    public Mono<LoanState> findByName(String name) {
        return repository.findByName(name).map(super::toEntity);
    }

    @Override
    public Flux<LoanState> findByNameNot(String name) {
        return repository.findByNameNot(name).map(super::toEntity);
    }
}
