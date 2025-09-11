package com.powerup.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths")
public class LoanPath {
    private String loans;
    private String loansForReview;
    private String loanDecision;
}
