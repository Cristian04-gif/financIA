package com.financia.kash.analisis.domain.model.analysisContext;

public record FinancialRecommendationItem(
        String code,
        RecommendationCategory category,
        RecommendationPriority priority,
        String title,
        String description,
        String action,
        String expectedBenefit) {

}
