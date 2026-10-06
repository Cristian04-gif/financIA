package com.financia.kash.presupuesto.infrastructure.adapter.database;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import com.financia.kash.presupuesto.application.model.BudgetExpense;
import com.financia.kash.presupuesto.application.port.output.BudgetExpensePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class BudgetExpenseAdapter implements BudgetExpensePort {
    private final DatabaseClient databaseClient;

    @Override
    public Flux<BudgetExpense> findExpensesByCategory(UUID userId, LocalDate start, LocalDate end) {
        return databaseClient.sql("""
                SELECT categoria_id, SUM(monto) AS gastado
                FROM movimientos
                WHERE usuario_id = :userId AND tipo = 'EGRESO'
                  AND fecha_emision BETWEEN :start AND :end
                GROUP BY categoria_id
                ORDER BY categoria_id
                """).bind("userId", userId).bind("start", start).bind("end", end)
                .map((row, metadata) -> new BudgetExpense(
                        row.get("categoria_id", UUID.class), row.get("gastado", BigDecimal.class)))
                .all();
    }
}
