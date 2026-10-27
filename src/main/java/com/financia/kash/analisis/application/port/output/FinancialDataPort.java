package com.financia.kash.analisis.application.port.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.analisis.domain.model.ExpenseByCategory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FinancialDataPort {
    Mono<FinancialTotals> getTotals(UUID userId, LocalDateTime startInclusive, LocalDateTime endExclusive);

    Flux<ExpenseByCategory> getExpensesByCategory(UUID userId, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    record FinancialTotals(BigDecimal income, BigDecimal expenses, BigDecimal savings) {
    }
}
