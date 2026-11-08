package com.financia.kash.movimiento.comprobante.infrastructure.database;

import java.time.Clock;
import java.time.LocalDate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import com.financia.kash.movimiento.comprobante.application.port.output.OcrMetricsPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class OcrMetricsAdapter implements OcrMetricsPort {
    private final DatabaseClient database;
    private final Clock reportingClock;

    public Mono<Void> record(boolean successful) {
        return Mono.defer(() -> database.sql("""
                INSERT INTO metricas_ocr_diarias(fecha, exitos, fallos)
                VALUES(:date, :success, :failure)
                ON CONFLICT (fecha) DO UPDATE
                SET exitos = metricas_ocr_diarias.exitos + EXCLUDED.exitos,
                    fallos = metricas_ocr_diarias.fallos + EXCLUDED.fallos
                """).bind("date", LocalDate.now(reportingClock))
                .bind("success", successful ? 1L : 0L).bind("failure", successful ? 0L : 1L).then());
    }
}
