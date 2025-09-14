package com.powerup.port.sqs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CapacityValidationMessage {
    private Long loanId;
    private BigDecimal loanAmount;
    private Integer loanTerm;
    private Double interestRate;
    private String applicantEmail;
    private BigDecimal applicantIncome;
    private List<ActiveLoanInfo> activeLoans;
}
