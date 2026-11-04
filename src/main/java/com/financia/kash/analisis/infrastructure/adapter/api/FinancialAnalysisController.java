package com.financia.kash.analisis.infrastructure.adapter.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.analisis.application.service.CompareFinancialPeriodsService;
import com.financia.kash.analisis.application.service.GetFinancialIndicatorsService;
import com.financia.kash.analisis.domain.model.indicators.FinancialIndicators;
import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class FinancialAnalysisController {
    private final GetFinancialIndicatorsService financialIndicatorsService;
    private final CompareFinancialPeriodsService compareFinancialPeriodsService;

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

}
