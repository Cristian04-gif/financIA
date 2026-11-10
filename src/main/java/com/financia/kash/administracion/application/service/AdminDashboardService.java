package com.financia.kash.administracion.application.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import com.financia.kash.administracion.application.port.input.GetAdminDashboardUseCase;
import com.financia.kash.administracion.application.port.output.AdminDashboardPort;
import com.financia.kash.administracion.domain.model.*;
import com.financia.kash.administracion.domain.model.AdminDashboard.DailyRegistrations;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AdminDashboardService implements GetAdminDashboardUseCase {
    private final AdminDashboardPort metrics;
    private final Clock reportingClock;

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Mono<AdminDashboard> get(LocalDate from, LocalDate to) {
        return Mono.defer(() -> {
            ReportingPeriod period = ReportingPeriod.resolve(from, to, reportingClock);
            return metrics.overview(period).flatMap(overview -> metrics.registrations(period)
                    .collectMap(DailyRegistrations::date, DailyRegistrations::count)
                    .map(counts -> new AdminDashboard(period, reportingClock.getZone().getId(), reportingClock.instant(),
                            overview, period.from().datesUntil(period.to().plusDays(1))
                                    .map(date -> new DailyRegistrations(date, counts.getOrDefault(date, 0L)))
                                    .collect(Collectors.toList()))));
        });
    }
}
