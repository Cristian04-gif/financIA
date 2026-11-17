package com.financia.kash.analisis.domain.model.diagnosis;

import java.time.LocalDate;
import java.util.List;

public record FinancialDiagnosis(
        LocalDate startDate,
        LocalDate endDate,
        int totalDiagnoses,
        List<FinancialDiagnosisItem> diagnoses

) {
}
