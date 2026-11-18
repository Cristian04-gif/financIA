package com.financia.kash.analisis.infrastructure.adapter.api.dto;

import java.math.BigDecimal;

import com.financia.kash.analisis.domain.model.diagnosis.FinancialDiagnosis;
import com.financia.kash.analisis.domain.model.finging.FinancialFindings;
import com.financia.kash.analisis.domain.model.indicators.FinancialIndicators;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicators;
import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;

import jakarta.validation.constraints.NotNull;

public record FinancialAnalysisContextRequest(
        @NotNull FinancialIndicators indicators,
        @NotNull FinancialPeriodComparison periodComparison,
        @NotNull BigDecimal savingsRate,
        @NotNull ObjectiveIndicators objectives,
        @NotNull FinancialFindings findings,
        @NotNull FinancialDiagnosis diagnosis) {

}
