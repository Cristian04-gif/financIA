package com.financia.kash.analisis.infrastructure.adapter.database.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.financia.kash.analisis.application.port.output.BudgetDataPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class R2dbcBudgetDataAdapter implements BudgetDataPort {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<BudgetInfo> findByIdAndUserId(UUID budgetId, UUID userId) {
        String sql = """
                    SELECT id, nombre, periodo_inicial, periodo_fin, monto_limite_total
                    FROM presupuestos
                    WHERE ID = :budgetId AND usuario_id = :userId
                """;

        return databaseClient.sql(sql)
                .bind("budgetId", budgetId)
                .bind("userId", userId)
                .map((row, metadata) -> new BudgetInfo(row.get("id", UUID.class),
                        row.get("nombre", String.class),
                        row.get("periodo_inicial", LocalDate.class),
                        row.get("periodo_fin", LocalDate.class),
                        row.get("monto_limite_total", BigDecimal.class)))
                .one();
    }

    @Override
    public Mono<BigDecimal> getTotalSpent(UUID userId, LocalDate startDate, LocalDate endDate) {
        String sql = """
                SELECT COALESCE(SUM(monto), 0) AS total_spent
                FROM movimientos
                WHERE usuario_id = :userId
                  AND tipo = 'EGRESO'
                  AND fecha_registro >= :startDate
                  AND fecha_registro < :endDateExclusive
                """;

        return databaseClient.sql(sql)
                .bind("userId", userId)
                .bind("startDate", startDate.atStartOfDay())
                .bind("endDateExclusive",
                        endDate.plusDays(1).atStartOfDay())
                .map((row, metadata) -> row.get("total_spent", BigDecimal.class))
                .one();
    }

    @Override
    public Flux<CategoryBudgetData> getCategorySpending(UUID budgetId, UUID userId, LocalDate startDate,
            LocalDate endDate) {
        String sql = """
                SELECT
                    bc.categoria_id AS category_id,
                    c.nombre AS category_name,
                    bc.monto_limite AS category_limit,
                    COALESCE(SUM(m.monto), 0) AS category_spent

                FROM presupuesto_categorias bc

                INNER JOIN categorias c
                    ON c.id = bc.categoria_id

                LEFT JOIN movimientos m
                    ON m.categoria_id = bc.categoria_id
                    AND m.usuario_id = :userId
                    AND m.tipo = 'EGRESO'
                    AND m.fecha_registro >= :startDate
                    AND m.fecha_registro < :endDateExclusive

                WHERE bc.presupuesto_id = :budgetId

                GROUP BY
                    bc.categoria_id,
                    c.nombre,
                    bc.monto_limite

                ORDER BY category_spent DESC
                """;

        return databaseClient.sql(sql)
                .bind("budgetId", budgetId)
                .bind("userId", userId)
                .bind("startDate", startDate.atStartOfDay())
                .bind("endDateExclusive",
                        endDate.plusDays(1).atStartOfDay())
                .map((row, metadata) -> new CategoryBudgetData(
                        row.get("category_id", UUID.class),
                        row.get("category_name", String.class),
                        row.get("category_limit", BigDecimal.class),
                        row.get("category_spent", BigDecimal.class)))
                .all();
    }

}
