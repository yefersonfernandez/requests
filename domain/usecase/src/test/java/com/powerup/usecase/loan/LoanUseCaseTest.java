package com.powerup.usecase.loan;

import com.powerup.exception.LoanTypeNotFoundException;
import com.powerup.exception.UserNotFoundException;
import com.powerup.model.loan.Loan;
import com.powerup.model.loan.gateways.ILoanRepositoryPort;
import com.powerup.model.loantype.LoanType;
import com.powerup.model.loantype.gateways.ILoanTypeRepositoryPort;
import com.powerup.port.consumer.IUserConsumerPort;
import com.powerup.port.consumer.model.UserConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanUseCaseTest {

    @Mock
    private ILoanRepositoryPort loanRepositoryPort;
    @Mock
    private ILoanTypeRepositoryPort loanTypeRepositoryPort;
    @Mock
    private IUserConsumerPort userConsumerPort;
    @InjectMocks
    private LoanUseCase loanUseCase;

    private Loan loan;
    private UserConsumer userConsumer;
    private LoanType loanType;

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
    }

    @Test
    @DisplayName("Must save a loan successfully when user and loan type are valid")
    void testSaveLoanSuccess() {
        when(userConsumerPort.getUserByIdentityDocument(loan.getIdentityDocument())).thenReturn(Mono.just(userConsumer));
        when(loanTypeRepositoryPort.findById(loan.getIdLoanType())).thenReturn(Mono.just(loanType));
        when(loanRepositoryPort.saveLoan(any(Loan.class))).thenReturn(Mono.just(loan));

        StepVerifier.create(loanUseCase.saveLoan(loan))
                .expectNext(loan)
                .verifyComplete();
    }

    @Test
    @DisplayName("Must return error if user is not found")
    void testSaveLoanUserNotFound() {
        when(userConsumerPort.getUserByIdentityDocument(loan.getIdentityDocument())).thenReturn(Mono.empty());

        StepVerifier.create(loanUseCase.saveLoan(loan))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Must return error if loan type is not found")
    void testSaveLoanLoanTypeNotFound() {
        when(userConsumerPort.getUserByIdentityDocument(loan.getIdentityDocument())).thenReturn(Mono.just(userConsumer));
        when(loanTypeRepositoryPort.findById(loan.getIdLoanType())).thenReturn(Mono.empty());

        StepVerifier.create(loanUseCase.saveLoan(loan))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }
}