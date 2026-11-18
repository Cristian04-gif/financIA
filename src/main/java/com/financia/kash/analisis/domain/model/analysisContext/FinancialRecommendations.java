package com.financia.kash.analisis.domain.model.analysisContext;

import java.time.LocalDate;
import java.util.List;

public record FinancialRecommendations(
        LocalDate startDate,
        LocalDate endDate,
        String generalAssessment,
        List<FinancialRecommendationItem> recommendations) {

}
