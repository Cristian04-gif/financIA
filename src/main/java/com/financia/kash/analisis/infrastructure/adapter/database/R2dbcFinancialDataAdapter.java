package com.financia.kash.analisis.infrastructure.adapter.database;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.financia.kash.analisis.application.port.output.FinancialDataPort;
import com.financia.kash.analisis.domain.model.ExpenseByCategory;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class R2dbcFinancialDataAdapter implements FinancialDataPort {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<FinancialTotals> getTotals(UUID userId, LocalDateTime startInclusive, LocalDateTime endExclusive) {
        String sql = """
                    SELECT
                    COALESCE(
                        SUM(CASE
                            WHEN tipo = 'INGRESO' THEN monto
                            ELSE 0
                        END),
                        0
                    ) AS income,

                    COALESCE(
                        SUM(CASE
                            WHEN tipo = 'EGRESO' THEN monto
                            ELSE 0
                        END),
                        0
                    ) AS expenses,

                    COALESCE(
                        SUM(CASE
                            WHEN tipo = 'AHORRO' THEN monto
                            ELSE 0
                        END),
                        0
                    ) AS savings

                FROM movimientos
                WHERE usuario_id = :userId
                  AND fecha_registro >= :startInclusive
                  AND fecha_registro < :endExclusive
                            """;
        return databaseClient.sql(sql)
                .bind("userId", userId)
                .bind("startInclusive", startInclusive)
                .bind("endExclusive", endExclusive)
                .map((row, metadata) -> new FinancialTotals(row.get("income", BigDecimal.class),
                        row.get("expenses", BigDecimal.class),
                        row.get("savings", BigDecimal.class)))
                .one();
    }

    @Override
    public Flux<ExpenseByCategory> getExpensesByCategory(UUID userId, LocalDateTime startInclusive,
            LocalDateTime endExclusive) {
        String sql = """
                   SELECT
                    m.categoria_id AS category_id,
                    COALESCE(c.nombre, 'Categoría no disponible')
                        AS category_name,
                    SUM(m.monto) AS amount

                FROM movimientos m
                LEFT JOIN categorias c
                    ON c.id = m.categoria_id

                WHERE m.usuario_id = :userId
                  AND m.tipo = 'EGRESO'
                  AND m.fecha_registro >= :startInclusive
                  AND m.fecha_registro < :endExclusive

                GROUP BY m.categoria_id, c.nombre
                ORDER BY SUM(m.monto) DESC
                    """;
        return databaseClient.sql(sql)
                .bind("userId", userId)
                .bind("startInclusive", startInclusive)
                .bind("endExclusive", endExclusive)
                .map((row, metadata) -> new ExpenseByCategory(
                        row.get("category_id", UUID.class),
                        row.get("category_name", String.class),
                        row.get("amount", BigDecimal.class)))
                .all();
    }

}
