package com.powerup.usecase.util;

import com.powerup.model.loan.Loan;
import com.powerup.port.consumer.model.UserConsumer;

public class LoanUtils {

    private LoanUtils() {}

    public static Loan buildLoanWithUserData(Loan loan, UserConsumer userConsumer) {
        return Loan.builder()
                .amount(loan.getAmount())
                .term(loan.getTerm())
                .email(userConsumer.getEmail())
                .idLoanType(loan.getIdLoanType())
                .idLoanState(1L)
                .build();
    }
}
