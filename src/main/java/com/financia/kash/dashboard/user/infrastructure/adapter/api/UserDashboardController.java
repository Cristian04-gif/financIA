package com.financia.kash.dashboard.user.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.dashboard.user.application.port.input.DashboardSummaryUseCase;
import com.financia.kash.dashboard.user.application.port.input.command.DashboardSummaryCommand;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/user/dashboard")
@RequiredArgsConstructor
@Log4j2
public class UserDashboardController {

    private final DashboardSummaryUseCase dashboardSummaryUseCase;

    @GetMapping()
    public Mono<ResponseEntity<DashboardSummary>> dashboard(@AuthenticationPrincipal UserEntity user,
            @RequestParam LocalDate from) {
        log.info("dashboard");
        return dashboardSummaryUseCase
                .dashboardSummary(new DashboardSummaryCommand(user.getId(), from, LocalDate.now()))
                .map(ResponseEntity::ok);
    }

}
