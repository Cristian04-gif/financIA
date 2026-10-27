package com.financia.kash.analisis.infrastructure.adapter.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.analisis.application.service.GetFinancialIndicatorsService;
import com.financia.kash.analisis.domain.model.FinancialIndicators;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class FinancialAnalysisController {
    private final GetFinancialIndicatorsService financialIndicatorsService;

    @GetMapping("/indicators")
    public Mono<ResponseEntity<FinancialIndicators>> financialIndicators(@AuthenticationPrincipal UserEntity user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return financialIndicatorsService.execute(user.getId(), startDate, endDate).map(ResponseEntity::ok);
    }
}
