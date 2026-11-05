package com.financia.kash.analisis.domain.model.budgetConsumtion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BudgetIndicators(
        UUID budgetId,
        String budgetName,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal totalLimit,
        BigDecimal totalSpent,
        BigDecimal totalAvailable,
        BigDecimal consumptionPercentage,
        BudgetConsumptionStatus status,
        List<CategoryBudgetIndicator> categories) {

}
