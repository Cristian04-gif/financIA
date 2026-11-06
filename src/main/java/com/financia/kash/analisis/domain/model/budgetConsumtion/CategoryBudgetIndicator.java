package com.financia.kash.analisis.domain.model.budgetConsumtion;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryBudgetIndicator(
        UUID categoryId,
        String categoryName,
        BigDecimal limit,
        BigDecimal spent,
        BigDecimal available,
        BigDecimal consumptionPercentage,
        BudgetConsumptionStatus status) {

}
