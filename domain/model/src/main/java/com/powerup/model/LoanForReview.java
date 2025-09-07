package com.powerup.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@Builder(toBuilder = true)
public class LoanForReview {
    private BigDecimal amount;
    private Integer term;
    private String email;
    private String clientName;
    private String loanType;
    private Double interestRate;
    private String loanStatus;
    private BigDecimal baseSalary;
    private BigDecimal totalMonthlyDebtApprovedLoans;
}
