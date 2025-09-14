package com.powerup.port.sqs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveLoanInfo {
    private BigDecimal amount;
    private Integer term;
    private Double interestRate;
}
