package com.powerup.enums;

import lombok.Getter;

@Getter
public enum LoanStatus {

    PENDING_REVIEW("PENDING_REVIEW"),
    MANUAL_REVIEW("MANUAL_REVIEW"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String status;

    LoanStatus(String status) {
        this.status = status;
    }

}
