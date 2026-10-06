package com.financia.kash.presupuesto.domain.exception;

public class BudgetValidationException extends RuntimeException {
    public BudgetValidationException(String message) {
        super(message);
    }
}

