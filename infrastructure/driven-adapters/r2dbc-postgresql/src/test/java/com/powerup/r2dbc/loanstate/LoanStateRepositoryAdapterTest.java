package com.powerup.r2dbc.loanstate;

import com.powerup.model.loanstate.LoanState;
import com.powerup.r2dbc.entity.LoanStateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanStateRepositoryAdapterTest {

    @Mock
    private ILoanStateRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private LoanStateRepositoryAdapter repositoryAdapter;

    private LoanState loanState;
    private LoanStateEntity loanStateEntity;

    @BeforeEach
    void setUp() {
        loanState = LoanState.builder()
                .id(1L)
                .name("PENDING")
                .build();

        loanStateEntity = LoanStateEntity.builder()
                        .id(loanState.getId())
                        .name(loanState.getName())
                        .build();

        lenient().when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Should find a loan state by name")
    void testFindByName() {
        when(repository.findByName("PENDING")).thenReturn(Mono.just(loanStateEntity));
        when(mapper.map(loanStateEntity, LoanState.class)).thenReturn(loanState);

        StepVerifier.create(repositoryAdapter.findByName("PENDING"))
                .expectNext(loanState)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when loan state by name not found")
    void testFindByNameEmpty() {
        when(repository.findByName("UNKNOWN")).thenReturn(Mono.empty());

        StepVerifier.create(repositoryAdapter.findByName("UNKNOWN"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find all loan states excluding a name")
    void testFindByNameNot() {
        LoanStateEntity entity1 = new LoanStateEntity();
        entity1.setId(2L);
        entity1.setName("APPROVED");

        LoanStateEntity entity2 = new LoanStateEntity();
        entity2.setId(3L);
        entity2.setName("REJECTED");

        when(repository.findByNameNot("PENDING")).thenReturn(Flux.just(entity1, entity2));
        when(mapper.map(entity1, LoanState.class)).thenReturn(new LoanState(2L, "APPROVED",""));
        when(mapper.map(entity2, LoanState.class)).thenReturn(new LoanState(3L, "REJECTED",""));

        StepVerifier.create(repositoryAdapter.findByNameNot("PENDING"))
                .expectNextMatches(ls -> ls.getId().equals(2L) && ls.getName().equals("APPROVED"))
                .expectNextMatches(ls -> ls.getId().equals(3L) && ls.getName().equals("REJECTED"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when no loan states excluding a name")
    void testFindByNameNotEmpty() {
        when(repository.findByNameNot(any(String.class))).thenReturn(Flux.empty());

        StepVerifier.create(repositoryAdapter.findByNameNot("PENDING"))
                .verifyComplete();
    }
}
