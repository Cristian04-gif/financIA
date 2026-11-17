package com.financia.kash.analisis.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.input.AnalyzeObjectivesUseCase;
import com.financia.kash.analisis.application.port.input.CompareFinancialPeriodsUseCase;
import com.financia.kash.analisis.application.port.input.GetFinancialIndicatorsUseCase;
import com.financia.kash.analisis.application.port.input.GetFinancialSummaryUseCase;
import com.financia.kash.analisis.domain.model.FinancialSummary;
import com.financia.kash.analisis.domain.model.indicators.FinancialIndicators;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicators;
import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetFinancialSummaryService implements GetFinancialSummaryUseCase {
    private final GetFinancialIndicatorsUseCase financialIndicatorsUseCase;
    private final CompareFinancialPeriodsUseCase compareFinancialPeriodsUseCase;
    private final AnalyzeObjectivesUseCase analyzeObjectivesUseCase;

    @Override
    public Mono<FinancialSummary> execute(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate) {

        if (userId == null || startDate == null || endDate == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "El usuario y las fechas son obligatorios"));
        }

        if (startDate.isAfter(endDate)) {
            return Mono.error(
                    new IllegalArgumentException(
                            "La fecha inicial no puede ser posterior a la final"));
        }

        return Mono.zip(
                financialIndicatorsUseCase.execute(
                        userId, startDate, endDate),
                compareFinancialPeriodsUseCase.execute(
                        userId, startDate, endDate),
                analyzeObjectivesUseCase.analyze(userId)).map(tuple -> {

                    FinancialIndicators indicators = tuple.getT1();
                    FinancialPeriodComparison comparison = tuple.getT2();
                    ObjectiveIndicators objectives = tuple.getT3();

                    BigDecimal savingsRate = calculateSavingsRate(
                            indicators.income(),
                            indicators.recordedSavings());

                    return new FinancialSummary(
                            startDate,
                            endDate,
                            "PEN",
                            indicators,
                            comparison,
                            savingsRate,
                            objectives);
                });
    }

    private BigDecimal calculateSavingsRate(
            BigDecimal income,
            BigDecimal savings) {

        if (income == null ||
                income.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return savings
                .divide(income, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
