package com.financia.kash.presupuesto.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.presupuesto.domain.exception.BudgetValidationException;

public record BudgetCategory(UUID id, UUID categoryId, BigDecimal amountLimit) {
    public BudgetCategory {
        if (categoryId == null) {
            throw new BudgetValidationException("La categoria es obligatoria");
        }
        amountLimit = BudgetAmounts.positive(amountLimit);
    }
}
