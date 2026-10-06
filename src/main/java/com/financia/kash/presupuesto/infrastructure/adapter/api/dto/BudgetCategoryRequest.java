package com.financia.kash.presupuesto.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.*;

public record BudgetCategoryRequest(
        @NotNull UUID categoryId,
        @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal amountLimit) {
}
