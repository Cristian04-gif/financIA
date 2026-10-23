package com.financia.kash.presupuesto.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record BudgetRequest(
        @NotBlank @Size(max = 255) String name,
        @NotNull LocalDate periodStart,
        @NotNull LocalDate periodEnd,
        @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal amountLimitTotal,
        @NotNull List<@NotNull @Valid BudgetCategoryRequest> categories) {
}
