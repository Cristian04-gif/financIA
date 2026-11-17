package com.financia.kash.analisis.domain.model.diagnosis;

public record FinancialDiagnosisItem(
    String code,
        DiagnosisType type,
        DiagnosisSeverity severity,
        String title,
        String description
) {

}
