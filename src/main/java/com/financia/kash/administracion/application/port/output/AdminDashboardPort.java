package com.financia.kash.administracion.application.port.output;

import com.financia.kash.administracion.domain.model.AdminDashboard.*;
import com.financia.kash.administracion.domain.model.ReportingPeriod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminDashboardPort {
    Mono<Overview> overview(ReportingPeriod period);
    Flux<DailyRegistrations> registrations(ReportingPeriod period);
}
