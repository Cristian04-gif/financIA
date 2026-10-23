package com.financia.kash.presupuesto.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BudgetConsumptionResponse(UUID budgetId, BigDecimal amountLimitTotal, BigDecimal spent,
        BigDecimal available, BigDecimal exceeded, List<BudgetCategoryConsumptionResponse> categories) {
}
