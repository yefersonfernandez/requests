package com.powerup.port.sqs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanDecisionMessage {
    private Long loanId;
    private String clientEmail;
    private String decision;
}