package com.financia.kash.analisis.application.port.input;

import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;

import reactor.core.publisher.Mono;

public interface CompareFinancialPeriodsUseCase {
    public Mono<FinancialPeriodComparison> execute(UUID userId, LocalDate startDate, LocalDate endDate);
}
