package com.powerup.r2dbc;

import com.powerup.model.loan.Loan;
import com.powerup.r2dbc.entity.LoanEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanRepositoryAdapterTest {

    @Mock
    private ILoanRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private LoanRepositoryAdapter repositoryAdapter;

    private Loan loan;
    private LoanEntity loanEntity;

    @BeforeEach
    void setUp() {
        loan = Loan.builder()
                .amount(BigDecimal.valueOf(5000))
                .term(12)
                .email("andres@gmail.com")
                .identityDocument("123")
                .idLoanType(1L)
                .idLoanState(1L)
                .build();

        loanEntity = LoanEntity.builder()
                .amount(loan.getAmount())
                .term(loan.getTerm())
                .email(loan.getEmail())
                .idLoanType(loan.getIdLoanType())
                .idLoanState(loan.getIdLoanState())
                .build();

        lenient().when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Should save a loan and return domain object")
    void testSaveLoan() {
        when(mapper.map(loan, LoanEntity.class)).thenReturn(loanEntity);
        when(repository.save(loanEntity)).thenReturn(Mono.just(loanEntity));
        when(mapper.map(loanEntity, Loan.class)).thenReturn(loan);

        StepVerifier.create(repositoryAdapter.saveLoan(loan))
                .expectNext(loan)
                .verifyComplete();
    }
}