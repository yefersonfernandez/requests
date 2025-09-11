package com.powerup.usecase.loan;

import com.powerup.exception.ForbiddenException;
import com.powerup.exception.LoanNotFoundException;
import com.powerup.exception.LoanStateNotFoundException;
import com.powerup.exception.LoanTypeNotFoundException;
import com.powerup.model.loan.Loan;
import com.powerup.model.loan.gateways.ILoanRepositoryPort;
import com.powerup.model.loanstate.LoanState;
import com.powerup.model.loanstate.gateways.ILoanStateRepositoryPort;
import com.powerup.model.loantype.LoanType;
import com.powerup.model.loantype.gateways.ILoanTypeRepositoryPort;
import com.powerup.port.consumer.IUserConsumerPort;
import com.powerup.port.consumer.model.UserConsumer;
import com.powerup.port.sqs.ISqsSenderPort;
import com.powerup.port.token.ISecurityContextPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanUseCaseTest {

    @Mock
    private ILoanRepositoryPort loanRepositoryPort;
    @Mock
    private ILoanTypeRepositoryPort loanTypeRepositoryPort;
    @Mock
    private ILoanStateRepositoryPort loanStateRepositoryPort;
    @Mock
    private IUserConsumerPort userConsumerPort;
    @Mock
    private ISecurityContextPort securityContextPort;
    @Mock
    private ISqsSenderPort iSqsSenderPort;

    @InjectMocks
    private LoanUseCase loanUseCase;

    private Loan loan;
    private UserConsumer userConsumer;
    private LoanType loanType;
    private LoanState pendingState;
    private LoanState approvedState;

    @BeforeEach
    void setUp() {
        loan = Loan.builder()
                .amount(BigDecimal.valueOf(5000))
                .term(2)
                .email("andres@gmail.com")
                .identityDocument("123")
                .idLoanState(1L)
                .idLoanType(1L)
                .build();

        userConsumer = UserConsumer.builder()
                .firstName("Andres")
                .lastName("Pru")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("cll")
                .phone("312009212")
                .identityDocument("123")
                .email("andres@gmail.com")
                .baseSalary(BigDecimal.valueOf(5000))
                .build();

        loanType = LoanType.builder()
                .name("Plus")
                .minAmount(BigDecimal.ZERO)
                .maxAmount(BigDecimal.valueOf(5000))
                .interestRate(0.1)
                .automaticValidation(true)
                .build();

        pendingState = LoanState.builder()
                .id(1L)
                .name("PENDING")
                .build();

        approvedState = LoanState.builder()
                .id(2L)
                .name("APPROVED")
                .build();
    }

    @Test
    @DisplayName("Must save a loan successfully when user and loan type are valid")
    void testSaveLoanSuccess() {
        when(securityContextPort.getUserEmail()).thenReturn(Mono.just("andres@gmail.com"));
        when(userConsumerPort.getUserByIdentityDocument(loan.getIdentityDocument())).thenReturn(Mono.just(userConsumer));
        when(loanTypeRepositoryPort.findById(loan.getIdLoanType())).thenReturn(Mono.just(loanType));
        when(loanRepositoryPort.saveLoan(any(Loan.class))).thenReturn(Mono.just(loan));

        StepVerifier.create(loanUseCase.saveLoan(loan))
                .expectNext(loan)
                .verifyComplete();
    }

    @Test
    @DisplayName("Must return ForbiddenException if token email does not match user email")
    void testSaveLoanForbidden() {
        when(securityContextPort.getUserEmail()).thenReturn(Mono.just("wrong@gmail.com"));
        when(userConsumerPort.getUserByIdentityDocument(loan.getIdentityDocument())).thenReturn(Mono.just(userConsumer));

        StepVerifier.create(loanUseCase.saveLoan(loan))
                .expectError(ForbiddenException.class)
                .verify();
    }

    @Test
    @DisplayName("Must return error if loan type is not found")
    void testSaveLoanLoanTypeNotFound() {
        when(securityContextPort.getUserEmail()).thenReturn(Mono.just("andres@gmail.com"));
        when(userConsumerPort.getUserByIdentityDocument(loan.getIdentityDocument())).thenReturn(Mono.just(userConsumer));
        when(loanTypeRepositoryPort.findById(loan.getIdLoanType())).thenReturn(Mono.empty());

        StepVerifier.create(loanUseCase.saveLoan(loan))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Must return loans filtered by valid status")
    void testGetLoansForReviewByValidStatus() {
        when(loanStateRepositoryPort.findByName(anyString())).thenReturn(Mono.just(pendingState));
        when(loanStateRepositoryPort.findByNameNot(anyString())).thenReturn(Flux.empty());
        when(loanRepositoryPort.findLoansForReview(anyLong(), anyInt(), anyInt()))
                .thenReturn(Flux.just(loan));
        when(loanStateRepositoryPort.findById(anyLong())).thenReturn(Mono.just(pendingState));
        when(loanTypeRepositoryPort.findById(anyLong())).thenReturn(Mono.just(loanType));
        when(userConsumerPort.getUserByEmail(anyString())).thenReturn(Mono.just(userConsumer));
        when(loanRepositoryPort.findLoansForReviewApprovedByEmail(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(loanUseCase.getLoansForReviewByStatus("PENDING", 0, 10))
                .expectNextMatches(l -> l.getEmail().equals("andres@gmail.com"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Must return empty if invalid status is provided")
    void testGetLoansForReviewByInvalidStatus() {
        when(loanStateRepositoryPort.findByName(anyString())).thenReturn(Mono.empty());
        when(loanStateRepositoryPort.findByNameNot(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(loanUseCase.getLoansForReviewByStatus("INVALID", 0, 10))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("Must return all loans excluding APPROVED when no status is provided")
    void testGetLoansForReviewNoStatus() {
        when(loanStateRepositoryPort.findByNameNot(anyString())).thenReturn(Flux.just(pendingState));
        when(loanRepositoryPort.findLoansForReview(anyLong(), anyInt(), anyInt()))
                .thenReturn(Flux.just(loan));
        when(loanStateRepositoryPort.findById(anyLong())).thenReturn(Mono.just(pendingState));
        when(loanTypeRepositoryPort.findById(anyLong())).thenReturn(Mono.just(loanType));
        when(userConsumerPort.getUserByEmail(anyString())).thenReturn(Mono.just(userConsumer));
        when(loanRepositoryPort.findLoansForReviewApprovedByEmail(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(loanUseCase.getLoansForReviewByStatus(null, 0, 10))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Must respect the size limit when fetching loans")
    void testGetLoansForReviewSizeLimit() {
        when(loanStateRepositoryPort.findByName(anyString())).thenReturn(Mono.just(pendingState));
        when(loanStateRepositoryPort.findByNameNot(anyString())).thenReturn(Flux.empty());
        when(loanRepositoryPort.findLoansForReview(anyLong(), anyInt(), anyInt()))
                .thenReturn(Flux.just(loan));
        when(loanStateRepositoryPort.findById(anyLong())).thenReturn(Mono.just(pendingState));
        when(loanTypeRepositoryPort.findById(anyLong())).thenReturn(Mono.just(loanType));
        when(userConsumerPort.getUserByEmail(anyString())).thenReturn(Mono.just(userConsumer));
        when(loanRepositoryPort.findLoansForReviewApprovedByEmail(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(loanUseCase.getLoansForReviewByStatus("PENDING", 0, 1))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Must calculate total monthly debt of approved loans")
    void testGetLoansForReviewTotalDebt() {
        Loan approvedLoan = Loan.builder()
                .amount(BigDecimal.valueOf(2000))
                .term(2)
                .email("andres@gmail.com")
                .identityDocument("123")
                .idLoanState(approvedState.getId())
                .idLoanType(1L)
                .build();

        when(loanStateRepositoryPort.findByNameNot(anyString())).thenReturn(Flux.just(approvedState));
        when(loanRepositoryPort.findLoansForReview(anyLong(), anyInt(), anyInt()))
                .thenReturn(Flux.just(approvedLoan));
        when(loanStateRepositoryPort.findById(anyLong())).thenReturn(Mono.just(approvedState));
        when(loanTypeRepositoryPort.findById(anyLong())).thenReturn(Mono.just(loanType));
        when(userConsumerPort.getUserByEmail(anyString())).thenReturn(Mono.just(userConsumer));
        when(loanRepositoryPort.findLoansForReviewApprovedByEmail(anyString()))
                .thenReturn(Flux.just(approvedLoan));

        StepVerifier.create(loanUseCase.getLoansForReviewByStatus(null, 0, 10))
                .expectNextMatches(l -> l.getTotalMonthlyDebtApprovedLoans().equals(BigDecimal.valueOf(1152.38)))
                .verifyComplete();
    }

    @Test
    @DisplayName("Must process loan decision successfully")
    void testProcessLoanDecisionSuccess() {
        when(loanRepositoryPort.findById(anyLong())).thenReturn(Mono.just(loan));
        when(loanStateRepositoryPort.findByName(anyString())).thenReturn(Mono.just(approvedState));
        when(loanRepositoryPort.saveLoan(any(Loan.class))).thenReturn(Mono.just(loan));
        when(iSqsSenderPort.sendMessage(any())).thenReturn(Mono.empty());

        StepVerifier.create(loanUseCase.processLoanDecision(1L, "APPROVED"))
                .expectNextMatches(l -> l.getIdLoanState().equals(approvedState.getId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Must return error if loan not found")
    void testProcessLoanDecisionLoanNotFound() {
        when(loanRepositoryPort.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(loanUseCase.processLoanDecision(20L, "APPROVED"))
                .expectError(LoanNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Must return error if loan state not found")
    void testProcessLoanDecisionStateNotFound() {
        when(loanRepositoryPort.findById(anyLong())).thenReturn(Mono.just(loan));
        when(loanStateRepositoryPort.findByName(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(loanUseCase.processLoanDecision(1L, "NON_EXISTENT_STATE"))
                .expectError(LoanStateNotFoundException.class)
                .verify();
    }

}
