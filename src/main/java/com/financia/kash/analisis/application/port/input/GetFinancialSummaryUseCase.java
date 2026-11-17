package com.financia.kash.analisis.application.port.input;

import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.analisis.domain.model.FinancialSummary;

import reactor.core.publisher.Mono;

public interface GetFinancialSummaryUseCase {
    Mono<FinancialSummary> execute(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate);
}
