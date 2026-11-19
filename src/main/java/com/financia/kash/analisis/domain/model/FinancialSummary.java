package com.financia.kash.analisis.domain.model;

import java.time.LocalDate;

import com.financia.kash.analisis.domain.model.indicators.FinancialIndicators;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicators;
import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;
import com.financia.kash.analisis.domain.model.savingsRate.SavingsRateIndicators;

public record FinancialSummary(
                LocalDate startDate,
                LocalDate endDate,
                String currency,
                FinancialIndicators financialIndicators,
                FinancialPeriodComparison periodComparison,
                SavingsRateIndicators savingsRate,
                ObjectiveIndicators objectives) {

}
