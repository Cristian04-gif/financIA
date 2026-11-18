package com.financia.kash.analisis.application.service;

import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.input.GenerateFinancialRecommendationsUseCase;
import com.financia.kash.analisis.application.port.output.FinancialRecommendationAIPort;
import com.financia.kash.analisis.domain.model.analysisContext.FinancialAnalysisContext;
import com.financia.kash.analisis.domain.model.analysisContext.FinancialRecommendations;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GenerateFinancialRecommendationsService implements GenerateFinancialRecommendationsUseCase {

    private final FinancialRecommendationAIPort financialRecommendationAIPort;

    @Override
    public Mono<FinancialRecommendations> execute(
            FinancialAnalysisContext context) {

        if (context == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "El contexto financiero es obligatorio"));
        }

        return financialRecommendationAIPort.generate(context);
    }

}
