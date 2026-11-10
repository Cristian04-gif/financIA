package com.financia.kash.administracion.application.port.input;

import java.time.LocalDate;
import com.financia.kash.administracion.domain.model.AdminDashboard;
import reactor.core.publisher.Mono;

public interface GetAdminDashboardUseCase {
    Mono<AdminDashboard> get(LocalDate from, LocalDate to);
}
