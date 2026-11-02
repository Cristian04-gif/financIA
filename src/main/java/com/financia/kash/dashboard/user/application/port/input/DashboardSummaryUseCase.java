package com.financia.kash.dashboard.user.application.port.input;

import com.financia.kash.dashboard.user.application.port.input.command.DashboardSummaryCommand;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary;

import reactor.core.publisher.Mono;

public interface DashboardSummaryUseCase {
    Mono<DashboardSummary> dashboardSummary(DashboardSummaryCommand command);
}
