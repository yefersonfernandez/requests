package com.powerup.r2dbc;

import com.powerup.model.loantype.LoanType;
import com.powerup.r2dbc.entity.LoanTypeEntity;
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
class LoanTypeRepositoryAdapterTest {

    @Mock
    private ILoanTypeRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private LoanTypeRepositoryAdapter repositoryAdapter;

    private LoanType loanType;
    private LoanTypeEntity loanTypeEntity;

    @BeforeEach
    void setUp() {
        loanType = LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(5000))
                .interestRate(0.05)
                .automaticValidation(true)
                .build();

        loanTypeEntity = LoanTypeEntity.builder()
                .id(loanType.getId())
                .name(loanType.getName())
                .minAmount(loanType.getMinAmount())
                .maxAmount(loanType.getMaxAmount())
                .interestRate(loanType.getInterestRate())
                .automaticValidation(loanType.getAutomaticValidation())
                .build();

        lenient().when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Should find a loan type by ID")
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Mono.just(loanTypeEntity));
        when(mapper.map(loanTypeEntity, LoanType.class)).thenReturn(loanType);

        StepVerifier.create(repositoryAdapter.findById(1L))
                .expectNext(loanType)
                .verifyComplete();
    }
}
