package com.financia.kash.presupuesto.infrastructure.adapter.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BudgetResponse(UUID id, UUID userId, String name, LocalDate periodStart, LocalDate periodEnd,
        BigDecimal amountLimitTotal, boolean active, LocalDateTime creationDate, LocalDateTime updateDate,
        List<BudgetCategoryResponse> categories) {
}

