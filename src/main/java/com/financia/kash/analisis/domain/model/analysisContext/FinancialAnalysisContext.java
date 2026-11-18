package com.financia.kash.analisis.domain.model.analysisContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.financia.kash.analisis.domain.model.diagnosis.FinancialDiagnosis;
import com.financia.kash.analisis.domain.model.finging.FinancialFindings;
import com.financia.kash.analisis.domain.model.indicators.FinancialIndicators;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicators;
import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;

public record FinancialAnalysisContext(
        LocalDate startDate,
        LocalDate endDate,
        String currency,
        FinancialIndicators indicators,
        FinancialPeriodComparison periodComparison,
        BigDecimal savingsRate,
        ObjectiveIndicators objectives,
        FinancialFindings findings,
        FinancialDiagnosis diagnosis) {

}
