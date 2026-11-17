package com.financia.kash.analisis.application.port.input;

import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.analisis.domain.model.indicators.FinancialIndicators;

import reactor.core.publisher.Mono;

public interface GetFinancialIndicatorsUseCase {
    public Mono<FinancialIndicators> execute(UUID userId, LocalDate startDate, LocalDate endDate);
}
