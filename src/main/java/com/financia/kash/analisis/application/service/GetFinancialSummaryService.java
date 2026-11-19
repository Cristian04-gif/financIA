package com.financia.kash.analisis.application.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.input.AnalyzeObjectivesUseCase;
import com.financia.kash.analisis.application.port.input.AnalyzeSavingsRateUseCase;
import com.financia.kash.analisis.application.port.input.CompareFinancialPeriodsUseCase;
import com.financia.kash.analisis.application.port.input.GetFinancialIndicatorsUseCase;
import com.financia.kash.analisis.application.port.input.GetFinancialSummaryUseCase;
import com.financia.kash.analisis.domain.model.FinancialSummary;
import com.financia.kash.analisis.domain.model.indicators.FinancialIndicators;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicators;
import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;
import com.financia.kash.analisis.domain.model.savingsRate.SavingsRateIndicators;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetFinancialSummaryService implements GetFinancialSummaryUseCase {
        private final GetFinancialIndicatorsUseCase financialIndicatorsUseCase;
        private final CompareFinancialPeriodsUseCase compareFinancialPeriodsUseCase;
        private final AnalyzeObjectivesUseCase analyzeObjectivesUseCase;
        private final AnalyzeSavingsRateUseCase analyzeSavingsRateUseCase;

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
                                analyzeObjectivesUseCase.analyze(userId),
                                analyzeSavingsRateUseCase.analyze(userId, startDate, endDate)).map(tuple -> {

                                        FinancialIndicators indicators = tuple.getT1();
                                        FinancialPeriodComparison comparison = tuple.getT2();
                                        ObjectiveIndicators objectives = tuple.getT3();
                                        SavingsRateIndicators savingsRate = tuple.getT4();

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

}
