package com.powerup.enums;

public enum ExceptionMessages {

    USER_NOT_FOUND("User with identity document '%s' does not exist"),
    LOAN_TYPE_NOT_FOUND("Loan type with id '%s' does not exist");;

    private final String message;

    ExceptionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }

}