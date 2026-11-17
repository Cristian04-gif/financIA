package com.financia.kash.analisis.application.port.input;

import com.financia.kash.analisis.domain.model.FinancialSummary;
import com.financia.kash.analisis.domain.model.diagnosis.FinancialDiagnosis;
import com.financia.kash.analisis.domain.model.finging.FinancialFindings;

import reactor.core.publisher.Mono;

public interface AnalyzeFinancialDiagnosisUseCase {
    public Mono<FinancialDiagnosis> analyze(
            FinancialSummary summary,
            FinancialFindings findings);
}
