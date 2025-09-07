package com.powerup.r2dbc.loan;

import com.powerup.model.loan.Loan;
import com.powerup.model.loan.gateways.ILoanRepositoryPort;
import com.powerup.r2dbc.entity.LoanEntity;
import com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class LoanRepositoryAdapter extends ReactiveAdapterOperations<Loan, LoanEntity, Long, ILoanRepository>  implements ILoanRepositoryPort {
    public LoanRepositoryAdapter(ILoanRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, Loan.class), transactionalOperator);
    }

    @Override
    public Mono<Loan> saveLoan(Loan loan) {
        return super.save(loan);
    }

    @Override
    public Flux<Loan> findLoansForReview(Long loanStateId, int page, int size) {
        return repository.findByIdLoanState(loanStateId, PageRequest.of(page, size))
                .map(super::toEntity);
    }

    @Override
    public Flux<Loan> findLoansForReviewApprovedByEmail(String email) {
        return repository.findAllByEmailAndIdLoanState(email, 3L)
                .map(super::toEntity);
    }
}
