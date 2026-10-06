package com.financia.kash.presupuesto.application.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BudgetConsumption(UUID budgetId, BigDecimal amountLimitTotal,
        BigDecimal spent, BigDecimal available, BigDecimal exceeded,
        List<BudgetCategoryConsumption> categories) {
    public BudgetConsumption {
        categories = List.copyOf(categories);
    }
}
