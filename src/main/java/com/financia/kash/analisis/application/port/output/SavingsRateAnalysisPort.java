package com.financia.kash.analisis.application.port.output;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import reactor.core.publisher.Flux;

public interface SavingsRateAnalysisPort {
    Flux<SavingsRateData> findSavingsRateData(
            UUID userId,
            LocalDate from,
            LocalDate until);

    public record SavingsRateData(
            LocalDate period,
            BigDecimal totalIncome,
            BigDecimal totalSavings) {
    }
}
