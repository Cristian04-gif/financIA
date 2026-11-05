package com.financia.kash.analisis.application.port.output;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BudgetDataPort {

    Mono<BudgetInfo> findByIdAndUserId(UUID budgetId, UUID userId);

    Mono<BigDecimal> getTotalSpent(UUID userid, LocalDate startDate, LocalDate endDate);

    Flux<CategoryBudgetData> getCategorySpending(UUID budgetId, UUID userId, LocalDate sDate, LocalDate endDate);

    record BudgetInfo(
            UUID id,
            String name,
            LocalDate periodStart,
            LocalDate periodEnd,
            BigDecimal totalLimit) {
    }

    record CategoryBudgetData(
            UUID categoryId,
            String categoryName,
            BigDecimal limit,
            BigDecimal spent) {
    }
}
