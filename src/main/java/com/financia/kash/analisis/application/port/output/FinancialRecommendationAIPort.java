package com.financia.kash.analisis.application.port.output;

import com.financia.kash.analisis.domain.model.analysisContext.FinancialAnalysisContext;
import com.financia.kash.analisis.domain.model.analysisContext.FinancialRecommendations;

import reactor.core.publisher.Mono;

public interface FinancialRecommendationAIPort {
    Mono<FinancialRecommendations> generate(
            FinancialAnalysisContext context);
}
