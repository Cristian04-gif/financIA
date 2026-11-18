package com.financia.kash.analisis.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.analisis.application.port.input.GenerateFinancialRecommendationsUseCase;
import com.financia.kash.analisis.domain.model.analysisContext.FinancialAnalysisContext;
import com.financia.kash.analisis.domain.model.analysisContext.FinancialRecommendations;
import com.financia.kash.analisis.infrastructure.adapter.api.dto.FinancialAnalysisContextRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/recommendation-ai")
@RequiredArgsConstructor
public class AIControllerRecommendation {

    private final GenerateFinancialRecommendationsUseCase financialRecommendationsUseCase;

    @PostMapping()
    public Mono<ResponseEntity<FinancialRecommendations>> financialRecommendations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestBody @Valid FinancialAnalysisContextRequest request) {
        return financialRecommendationsUseCase.execute(new FinancialAnalysisContext(startDate, endDate, "PEN",
                request.indicators(), request.periodComparison(), request.savingsRate(), request.objectives(),
                request.findings(), request.diagnosis())).map(ResponseEntity::ok);
    }

}
