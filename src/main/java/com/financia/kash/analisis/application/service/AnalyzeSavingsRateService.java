package com.financia.kash.analisis.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.input.AnalyzeSavingsRateUseCase;
import com.financia.kash.analisis.application.port.output.SavingsRateAnalysisPort;
import com.financia.kash.analisis.domain.model.savingsRate.SavingsRateIndicators;
import com.financia.kash.analisis.domain.model.savingsRate.SavingsRatePeriod;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnalyzeSavingsRateService implements AnalyzeSavingsRateUseCase {
    private final SavingsRateAnalysisPort port;

    @Override
    public Mono<SavingsRateIndicators> analyze(UUID userId, LocalDate from, LocalDate until) {
        return port.findSavingsRateData(userId, from, until)
                .map(data -> new SavingsRatePeriod(data.period(), data.totalIncome(), data.totalSavings(),
                        calculateSavingsRate(
                                data.totalIncome(),
                                data.totalSavings())))
                .collectList().map(periods -> new SavingsRateIndicators("PEN", periods));
    }

    private BigDecimal calculateSavingsRate(BigDecimal totalIncome, BigDecimal totalSavings) {
        if (totalIncome == null || totalIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return totalSavings
                .divide(
                        totalIncome,
                        4,
                        RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
