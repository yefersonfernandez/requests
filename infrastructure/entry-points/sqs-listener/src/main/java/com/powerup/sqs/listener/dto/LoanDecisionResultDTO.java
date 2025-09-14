package com.powerup.sqs.listener.dto;

public record LoanDecisionResultDTO (
        Long loanId,
        String decision
) {}
