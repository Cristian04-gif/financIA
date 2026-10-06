package com.financia.kash.presupuesto.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetCategoryConsumptionResponse(UUID categoryId, BigDecimal amountLimit,
        BigDecimal spent, BigDecimal available, BigDecimal exceeded) {
}

