package com.financia.kash.presupuesto.application.model;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetCategoryConsumption(UUID categoryId, BigDecimal amountLimit,
        BigDecimal spent, BigDecimal available, BigDecimal exceeded) {
}

