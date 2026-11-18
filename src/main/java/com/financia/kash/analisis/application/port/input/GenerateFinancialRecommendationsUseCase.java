package com.financia.kash.analisis.application.port.input;

import com.financia.kash.analisis.domain.model.analysisContext.FinancialAnalysisContext;
import com.financia.kash.analisis.domain.model.analysisContext.FinancialRecommendations;

import reactor.core.publisher.Mono;

public interface GenerateFinancialRecommendationsUseCase {
    Mono<FinancialRecommendations> execute(
            FinancialAnalysisContext context);
}
