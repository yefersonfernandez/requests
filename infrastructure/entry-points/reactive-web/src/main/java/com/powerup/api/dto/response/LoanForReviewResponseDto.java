package com.powerup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanForReviewResponseDto {
    private BigDecimal amount;
    private Integer term;
    private String email;
    private String clientName;
    private String loanType;
    private BigDecimal interestRate;
    private String loanStatus;
    private BigDecimal baseSalary;
    private BigDecimal totalMonthlyDebtApprovedLoans;
}