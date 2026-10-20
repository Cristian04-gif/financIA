package com.financia.kash.meta.infrastructure.database.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.financia.kash.meta.domain.enums.ObjectiveState;
import com.financia.kash.meta.infrastructure.database.repository.project.ContributionProject;
import com.financia.kash.shared.domain.PaginationRequest;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@Repository
@AllArgsConstructor
public class ContributionEntityQueryRepository {
    private final DatabaseClient databaseClient;

    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "fecha_creacion", "am.fecha_creacion",
            "monto", "am.monto",
            "id", "am.id");

    public Flux<ContributionProject> findAllContributionByObjective(UUID objectiveId, PaginationRequest request) {
        String column = SORT_COLUMNS.getOrDefault(request.getSortBy(), "am.fecha_creacion");

        String order = "asc".equalsIgnoreCase(request.getDirection()) ? "ASC" : "DESC";

        String sql = """
                SELECT  am.id,
                        m.id AS ID
                        m.nombre AS name_objective
                        m.descripcion AS description_objective
                        m.monto_actual AS currentAmount
                        m.monto_objetivo AS targetAmount
                        m.progreso AS percentageProgress
                        m.estado AS status
                        am.monto AS amount
                        am.fecha AS date
                        am.descripcion AS description
                FROM metas_financieras m
                INNER JOIN aportes_meta am ON m.id = am.meta_id
                WHERE m.id = :objectiveId
                ORDER BY %s %s, am.id DESC
                LIMIT :size OFFSET :offset
                """.formatted(column, order);

        return databaseClient.sql(sql)
                .bind("objectiveId", objectiveId)
                .bind("size", request.getPageSize())
                .bind("offset", (long) request.getPageNum() * request.getPageSize())
                .map((row, metadata) -> {
                    UUID id = row.get("id", UUID.class);
                    UUID objective_id = row.get("objective_id", UUID.class);
                    String name_objective = row.get("name_objective", String.class);
                    String description_objective = row.get("description_objective", String.class);
                    BigDecimal currentAmount = row.get("currentAmount", BigDecimal.class);
                    BigDecimal targetAmount = row.get("targetAmount", BigDecimal.class);
                    Double percentageProgress = row.get("percentageProgress", Double.class);
                    ObjectiveState status = row.get("status", ObjectiveState.class);
                    BigDecimal amount = row.get("amount", BigDecimal.class);
                    LocalDate date = row.get("date", LocalDate.class);
                    String description = row.get("description", String.class);

                    return new ContributionProject(id, objective_id, name_objective, description_objective,
                            currentAmount, targetAmount,
                            percentageProgress, status, amount, date, description);
                }).all();
    }

}
