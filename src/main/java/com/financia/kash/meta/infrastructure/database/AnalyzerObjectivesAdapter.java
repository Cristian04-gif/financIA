package com.financia.kash.meta.infrastructure.database;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.financia.kash.analisis.application.port.output.ObjectiveAnalysisPort;
import com.financia.kash.analisis.domain.model.objectives.ContributionData;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveData;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class AnalyzerObjectivesAdapter implements ObjectiveAnalysisPort {

    private final DatabaseClient databaseClient;

    @Override
    public Flux<ObjectiveData> findObjectives(UUID userId) {
        String sql = """
                SELECT id, nombre, monto_objetivo, monto_actual, fecha_inicio, fecha_objetivo
                FROM metas_financieras
                WHERE usuario_id = :userId
                    """;

        return databaseClient.sql(sql)
                .bind("userId", userId)
                .map((row, metadata) -> {
                    UUID id = row.get("id", UUID.class);
                    String name = row.get("nombre", String.class);
                    BigDecimal targetAmount = row.get("monto_objetivo", BigDecimal.class);
                    BigDecimal currentAmount = row.get("monto_actual", BigDecimal.class);
                    LocalDate startDate = row.get("fecha_inicio", LocalDate.class);
                    LocalDate targetDate = row.get("fecha_objetivo", LocalDate.class);
                    return new ObjectiveData(id, name, targetAmount, currentAmount, startDate, targetDate);
                }).all();
    }

    @Override
    public Flux<ContributionData> findContributions(UUID userId, UUID objectiveId) {
        String sql = """
                    SELECT ap.meta_id, ap.monto, ap.fecha
                    FROM aportes_meta ap
                    INNER JOIN metas_financieras m ON ap.meta_id = m.id
                    WHERE m.usuario_id = :userId AND ap.meta_id = :objectiveId
                """;

        return databaseClient.sql(sql)
                .bind("userId", userId)
                .bind("objectiveId", objectiveId)
                .map((row, metadata) -> {
                    UUID objective = row.get("meta_id", UUID.class);
                    BigDecimal amount = row.get("monto", BigDecimal.class);
                    LocalDate date = row.get("fecha", LocalDate.class);
                    return new ContributionData(objective, amount, date);
                }).all();
    }

}
