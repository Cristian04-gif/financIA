package com.financia.kash.analisis.infrastructure.adapter.api.dto;

import com.financia.kash.analisis.domain.model.FinancialSummary;
import com.financia.kash.analisis.domain.model.finging.FinancialFindings;

import jakarta.validation.constraints.NotNull;

public record FinancialDiagnosisRequest(@NotNull FinancialSummary summary,
        @NotNull FinancialFindings findings) {

}
