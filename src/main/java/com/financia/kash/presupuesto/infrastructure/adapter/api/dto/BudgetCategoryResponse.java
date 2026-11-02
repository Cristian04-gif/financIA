package com.financia.kash.presupuesto.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetCategoryResponse(UUID id, UUID categoryId, BigDecimal amountLimit) {
}
