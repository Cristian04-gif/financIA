package com.financia.kash.analisis.infrastructure.adapter.api;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.analisis.application.port.input.AnalyzeFinancialFindingsUseCase;
import com.financia.kash.analisis.application.port.input.AnalyzeObjectivesUseCase;
import com.financia.kash.analisis.application.port.input.AnalyzeSavingsRateUseCase;
import com.financia.kash.analisis.application.port.input.GetFinancialSummaryUseCase;
import com.financia.kash.analisis.application.service.CompareFinancialPeriodsService;
import com.financia.kash.analisis.application.service.GetBudgetIndicatorsService;
import com.financia.kash.analisis.application.service.GetFinancialIndicatorsService;
import com.financia.kash.analisis.domain.model.FinancialFindings;
import com.financia.kash.analisis.domain.model.FinancialSummary;
import com.financia.kash.analisis.domain.model.budgetConsumtion.BudgetIndicators;
import com.financia.kash.analisis.domain.model.indicators.FinancialIndicators;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicators;
import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;
import com.financia.kash.analisis.domain.model.savingsRate.SavingsRateIndicators;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class FinancialAnalysisController {
    private final GetFinancialIndicatorsService financialIndicatorsService;
    private final CompareFinancialPeriodsService compareFinancialPeriodsService;
    private final GetBudgetIndicatorsService getBudgetIndicatorsService;
    private final AnalyzeObjectivesUseCase analyzeObjectivesUseCase;
    private final AnalyzeSavingsRateUseCase analyzeSavingsRateUseCase;

    private final GetFinancialSummaryUseCase getFinancialSummaryUseCase;

    private final AnalyzeFinancialFindingsUseCase analyzeFinancialFindingsUseCase;

    @GetMapping("/indicators")
    public Mono<ResponseEntity<FinancialIndicators>> financialIndicators(@AuthenticationPrincipal UserEntity user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return financialIndicatorsService.execute(user.getId(), startDate, endDate).map(ResponseEntity::ok);
    }

    @GetMapping("/comparasion")
    public Mono<ResponseEntity<FinancialPeriodComparison>> financialPeriodComparison(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return compareFinancialPeriodsService.execute(user.getId(), startDate, endDate).map(ResponseEntity::ok);
    }

    @GetMapping("/budget/{id}")
    public Mono<ResponseEntity<BudgetIndicators>> budgetIndicators(@AuthenticationPrincipal UserEntity user,
            @PathVariable UUID id) {
        return getBudgetIndicatorsService.execute(id, user.getId()).map(ResponseEntity::ok);
    }

    @GetMapping("/objectives")
    public Mono<ResponseEntity<ObjectiveIndicators>> objectiveIndicators(@AuthenticationPrincipal UserEntity user) {
        return analyzeObjectivesUseCase.analyze(user.getId()).map(ResponseEntity::ok);
    }

    @GetMapping("/saving-rate")
    public Mono<ResponseEntity<SavingsRateIndicators>> savingsRateIndicators(@AuthenticationPrincipal UserEntity user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return analyzeSavingsRateUseCase.analyze(user.getId(), startDate, endDate).map(ResponseEntity::ok);
    }

    @GetMapping("/summary")
    public Mono<ResponseEntity<FinancialSummary>> financialSummary(@AuthenticationPrincipal UserEntity user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return getFinancialSummaryUseCase.execute(user.getId(), startDate, endDate).map(ResponseEntity::ok);
    }

    @PostMapping("/financial-findings")
    public Mono<ResponseEntity<FinancialFindings>> financialFindings(@RequestBody FinancialSummary summary) {

        return analyzeFinancialFindingsUseCase.analyze(summary).map(ResponseEntity::ok);
    }

}
