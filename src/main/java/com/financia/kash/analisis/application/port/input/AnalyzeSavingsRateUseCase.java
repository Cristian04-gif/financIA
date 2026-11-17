package com.financia.kash.analisis.application.port.input;

import java.time.LocalDate;
import java.util.UUID;

import com.financia.kash.analisis.domain.model.savingsRate.SavingsRateIndicators;

import reactor.core.publisher.Mono;

public interface AnalyzeSavingsRateUseCase {
    Mono<SavingsRateIndicators> analyze(
            UUID userId,
            LocalDate from,
            LocalDate until);
}
