package com.powerup.model.loan.gateways;

import com.powerup.model.loan.Loan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ILoanRepositoryPort {
    Mono<Loan> saveLoan(Loan loan);
    Flux<Loan> findLoansForReview(Long states, int page, int size);
    Flux<Loan> findLoansForReviewApprovedByEmail(String email);
    Mono<Loan> findById(Long id);
}
