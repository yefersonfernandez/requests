package com.powerup.port.sqs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanApprovedMessage {
    private String loanId;
    private BigDecimal amount;
    private Instant approvedAt;
}
