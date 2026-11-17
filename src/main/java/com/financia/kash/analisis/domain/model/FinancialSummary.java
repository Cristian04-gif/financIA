package com.financia.kash.analisis.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.financia.kash.analisis.domain.model.indicators.FinancialIndicators;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicators;
import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;

public record FinancialSummary(
        LocalDate startDate,
        LocalDate endDate,
        String currency,
        FinancialIndicators financialIndicators,
        FinancialPeriodComparison periodComparison,
        BigDecimal savingsRate,
        ObjectiveIndicators objectives) {

}
