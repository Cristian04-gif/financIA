package com.financia.kash.presupuesto.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.financia.kash.presupuesto.domain.exception.BudgetValidationException;

final class BudgetAmounts {
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("99999999999999999.99");

    private BudgetAmounts() {
    }

    static BigDecimal positive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.compareTo(MAX_AMOUNT) > 0) {
            throw new BudgetValidationException("El limite debe ser positivo y caber en NUMERIC(19,2)");
        }
        try {
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new BudgetValidationException("El limite admite como maximo dos decimales");
        }
    }
}

