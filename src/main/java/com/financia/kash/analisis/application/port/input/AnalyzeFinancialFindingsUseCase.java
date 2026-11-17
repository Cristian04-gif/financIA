package com.financia.kash.analisis.application.port.input;

import com.financia.kash.analisis.domain.model.FinancialFindings;
import com.financia.kash.analisis.domain.model.FinancialSummary;

import reactor.core.publisher.Mono;

public interface AnalyzeFinancialFindingsUseCase {
    Mono<FinancialFindings> analyze(FinancialSummary summary);

}
