package com.financia.kash.administracion.infrastructure.api;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.financia.kash.administracion.application.port.input.GetAdminDashboardUseCase;
import com.financia.kash.administracion.domain.model.AdminDashboard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Administracion - Dashboard")
public class AdminDashboardController {
    private final GetAdminDashboardUseCase dashboard;

    @GetMapping
    @Operation(summary = "Resumen operativo agregado para administradores",
            description = "Requiere ADMIN activo. Fechas inclusivas, maximo 366 dias y sin fechas futuras. "
                    + "Por defecto: primer dia del mes hasta hoy en REPORTING_TIME_ZONE. "
                    + "Los totales reflejan el estado actual; registros, altas de presupuestos, inicios de metas "
                    + "y OCR corresponden al periodo. No contiene datos financieros personales.")
    public Mono<AdminDashboard> get(
            @Parameter(description = "Inicio inclusivo YYYY-MM-DD; por defecto, primer dia del mes de to")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fin inclusivo YYYY-MM-DD; por defecto, hoy en la zona de reportes")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return dashboard.get(from, to);
    }
}
