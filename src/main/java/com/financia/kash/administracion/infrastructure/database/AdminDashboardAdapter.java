package com.financia.kash.administracion.infrastructure.database;

import java.time.LocalDate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import com.financia.kash.administracion.application.port.output.AdminDashboardPort;
import com.financia.kash.administracion.domain.model.AdminDashboard.*;
import com.financia.kash.administracion.domain.model.ReportingPeriod;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AdminDashboardAdapter implements AdminDashboardPort {
    private final DatabaseClient database;

    private static final String OVERVIEW = """
            WITH user_metrics AS (
                SELECT COUNT(*) FILTER (WHERE rol = 'USER') AS total_users,
                    COUNT(*) FILTER (WHERE rol = 'USER' AND estado = 'ACTIVO') AS active_users,
                    COUNT(*) FILTER (WHERE rol = 'USER' AND estado = 'SUSPENDIDO') AS suspended_users,
                    COUNT(*) FILTER (WHERE rol = 'USER' AND estado = 'INACTIVO') AS inactive_users,
                    COUNT(*) FILTER (WHERE rol = 'USER' AND estado = 'ELIMINADO') AS deleted_users,
                    COUNT(*) FILTER (WHERE rol = 'USER' AND estado = 'PENDIENTE') AS pending_users,
                    COUNT(*) FILTER (WHERE rol = 'ADMIN') AS administrators,
                    COUNT(*) FILTER (WHERE rol = 'USER' AND fecha_creacion BETWEEN :from AND :to) AS registered_users
                FROM usuarios
            ), category_metrics AS (
                SELECT COUNT(*) AS total_categories,
                    COUNT(*) FILTER (WHERE activo) AS active_categories,
                    COUNT(*) FILTER (WHERE NOT activo) AS inactive_categories
                FROM categorias WHERE usuario_id IS NULL AND categoria_padre_id IS NULL
            ), budget_metrics AS (
                SELECT COUNT(*) AS total_budgets,
                    COUNT(*) FILTER (WHERE p.activo) AS active_budgets,
                    COUNT(*) FILTER (WHERE NOT p.activo) AS inactive_budgets,
                    COUNT(*) FILTER (WHERE p.fecha_creacion >= :start AND p.fecha_creacion < :end) AS created_budgets
                FROM presupuestos p JOIN usuarios u ON u.id = p.usuario_id WHERE u.rol = 'USER'
            ), goal_metrics AS (
                SELECT COUNT(*) AS total_goals,
                    COUNT(*) FILTER (WHERE m.estado = 'COMPLETADA') AS completed_goals,
                    COUNT(*) FILTER (WHERE m.fecha_inicio BETWEEN :from AND :to) AS started_goals
                FROM metas_financieras m JOIN usuarios u ON u.id = m.usuario_id WHERE u.rol = 'USER'
            ), ocr_metrics AS (
                SELECT COALESCE(SUM(exitos) FILTER (WHERE fecha BETWEEN :from AND :to), 0)::bigint AS successful_ocr,
                    COALESCE(SUM(fallos) FILTER (WHERE fecha BETWEEN :from AND :to), 0)::bigint AS failed_ocr,
                    MIN(fecha) AS first_recorded_date
                FROM metricas_ocr_diarias
            )
            SELECT * FROM user_metrics CROSS JOIN category_metrics CROSS JOIN budget_metrics
                CROSS JOIN goal_metrics CROSS JOIN ocr_metrics
            """;

    public Mono<Overview> overview(ReportingPeriod period) {
        return database.sql(OVERVIEW).bind("from", period.from()).bind("to", period.to())
                .bind("start", period.from().atStartOfDay()).bind("end", period.to().plusDays(1).atStartOfDay())
                .map((row, metadata) -> new Overview(
                        new Users(count(row, "total_users"), count(row, "active_users"), count(row, "suspended_users"),
                                count(row, "inactive_users"), count(row, "deleted_users"), count(row, "pending_users"),
                                count(row, "administrators"), count(row, "registered_users")),
                        new Categories(count(row, "total_categories"), count(row, "active_categories"), count(row, "inactive_categories")),
                        new Budgets(count(row, "total_budgets"), count(row, "active_budgets"), count(row, "inactive_budgets"), count(row, "created_budgets")),
                        new Goals(count(row, "total_goals"), count(row, "completed_goals"), count(row, "started_goals")),
                        new Ocr(count(row, "successful_ocr"), count(row, "failed_ocr"),
                                count(row, "successful_ocr") + count(row, "failed_ocr"), row.get("first_recorded_date", LocalDate.class))))
                .one();
    }

    public Flux<DailyRegistrations> registrations(ReportingPeriod period) {
        return database.sql("""
                SELECT fecha_creacion AS date, COUNT(*) AS count FROM usuarios
                WHERE rol = 'USER' AND fecha_creacion BETWEEN :from AND :to
                GROUP BY fecha_creacion ORDER BY fecha_creacion
                """).bind("from", period.from()).bind("to", period.to())
                .map((row, metadata) -> new DailyRegistrations(row.get("date", LocalDate.class), count(row, "count"))).all();
    }

    private long count(Row row, String column) { return row.get(column, Long.class); }
}
