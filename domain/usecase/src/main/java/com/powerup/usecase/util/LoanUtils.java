package com.powerup.usecase.util;

import com.powerup.model.LoanForReview;
import com.powerup.model.loan.Loan;
import com.powerup.model.loanstate.LoanState;
import com.powerup.model.loantype.LoanType;
import com.powerup.port.consumer.model.UserConsumer;
import com.powerup.port.sqs.model.ActiveLoanInfo;
import com.powerup.port.sqs.model.CapacityValidationMessage;
import com.powerup.port.sqs.model.LoanApprovedMessage;
import com.powerup.port.sqs.model.LoanDecisionMessage;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@UtilityClass
public class LoanUtils {

    public static Loan buildLoanWithUserData(Loan loan, UserConsumer userConsumer) {
        return Loan.builder()
                .amount(loan.getAmount())
                .term(loan.getTerm())
                .email(userConsumer.getEmail())
                .idLoanType(loan.getIdLoanType())
                .idLoanState(1L)
                .build();
    }

    public static ActiveLoanInfo buildActiveLoanInfo(Loan loan, LoanType loanType) {
        return ActiveLoanInfo.builder()
                .amount(loan.getAmount())
                .term(loan.getTerm())
                .interestRate(loanType.getInterestRate())
                .build();
    }

    public static CapacityValidationMessage buildCapacityValidationMessage(
            Loan loan, UserConsumer user, LoanType currentLoanType, List<ActiveLoanInfo> activeLoans
    ) {
        return CapacityValidationMessage.builder()
                .loanId(loan.getId())
                .loanAmount(loan.getAmount())
                .loanTerm(loan.getTerm())
                .interestRate(currentLoanType.getInterestRate())
                .applicantEmail(user.getEmail())
                .applicantIncome(user.getBaseSalary())
                .activeLoans(activeLoans)
                .build();
    }

    public static LoanDecisionMessage buildLoanDecisionMessage(Loan loan, String decision) {
        return LoanDecisionMessage.builder()
                .loanId(loan.getId())
                .clientEmail(loan.getEmail())
                .decision(decision)
                .build();
    }

    public static LoanForReview buildLoanForReview(Loan loan, UserConsumer user, LoanType type, LoanState state, BigDecimal totalMonthlyDebt) {
        return LoanForReview.builder()
                .amount(loan.getAmount())
                .term(loan.getTerm())
                .email(loan.getEmail())
                .loanStatus(state.getName())
                .loanType(type.getName())
                .interestRate(type.getInterestRate())
                .clientName(user.getFirstName() + " " + user.getLastName())
                .baseSalary(user.getBaseSalary())
                .totalMonthlyDebtApprovedLoans(totalMonthlyDebt)
                .build();
    }

    public static LoanApprovedMessage buildLoanApprovedMessage(Loan savedLoan) {
        return LoanApprovedMessage.builder()
                .loanId(savedLoan.getId().toString())
                .amount(savedLoan.getAmount())
                .approvedAt(Instant.now())
                .build();
    }

    public static Mono<BigDecimal> calculateMonthlyInstallment(BigDecimal loanAmount, Integer loanTermMonths, Double interestRatePerMonth) {
        return Mono.justOrEmpty(loanAmount)
                .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
                .flatMap(amount -> Mono.justOrEmpty(loanTermMonths)
                        .filter(term -> term > 0)
                        .flatMap(term -> Mono.justOrEmpty(interestRatePerMonth)
                                .filter(rate -> rate > 0)
                                .map(rate -> {
                                    BigDecimal monthlyRate = BigDecimal.valueOf(rate);
                                    BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(term);
                                    BigDecimal numerator = amount.multiply(monthlyRate).multiply(factor);
                                    BigDecimal denominator = factor.subtract(BigDecimal.ONE);
                                    return numerator.divide(denominator, 10, RoundingMode.HALF_UP)
                                            .setScale(2, RoundingMode.HALF_UP);
                                })
                                .switchIfEmpty(Mono.just(amount.divide(BigDecimal.valueOf(term), 2, RoundingMode.HALF_UP)))
                        )
                )
                .switchIfEmpty(Mono.just(BigDecimal.ZERO));
    }
}
