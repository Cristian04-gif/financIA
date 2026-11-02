package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.financia.kash.dashboard.user.domain.model.ExpensesCategory.CategoriesExcenses;
import com.financia.kash.dashboard.user.domain.model.MovementsMonth;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.project.MovementProject;
import com.financia.kash.shared.domain.PaginationRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class MovementEntityQueryrepository {

        private final DatabaseClient databaseClient;

        private final static Map<String, String> SOIRT_COLUMN = Map.of(
                        "fecha_registro", "m.fecha_registro",
                        "monto", "m.monto",
                        "id", "m.id"

        );

        public Flux<MovementProject> findAllByUserId(UUID userId, PaginationRequest request) {
                String column = SOIRT_COLUMN.getOrDefault(request.getSortBy(), "m.fecha_registro");

                String order = "asc".equalsIgnoreCase(request.getDirection()) ? "ASC" : "DESC";

                String sql = """
                                SELECT
                                        m.id AS movement_id,
                                        cu.id AS account_id,
                                        cu.nombre AS account_name,
                                        ca.id AS category_id,
                                        ca.nombre AS category_name,
                                        ca.tipo AS category_type,
                                        m.monto AS monto,
                                        m.descripcion AS descripcion,
                                        m.fecha_registro AS date_issue,
                                        m.url_comprobante AS url_comprobate
                                        FROM movimientos m
                                    INNER JOIN cuentas cu ON m.cuenta_id = cu.id
                                    INNER JOIN categorias ca ON m.categoria_id = ca.id
                                    WHERE m.usuario_id = :userId
                                    ORDER BY %s %s, m.id
                                    LIMIT :size OFFSET :offset
                                """.formatted(column, order);

                return databaseClient.sql(sql)
                                .bind("userId", userId)
                                .bind("size", request.getPageSize())
                                .bind("offset", (long) request.getPageNum() * request.getPageSize())
                                .map((row, metadata) -> {
                                        UUID movement_id = row.get("movement_id", UUID.class);
                                        UUID account_id = row.get("account_id", UUID.class);
                                        String account_name = row.get("account_name", String.class);
                                        UUID category_id = row.get("category_id", UUID.class);
                                        String category_name = row.get("category_name", String.class);
                                        String category_type = row.get("category_type", String.class);
                                        BigDecimal monto = row.get("monto", BigDecimal.class);
                                        String descripcion = row.get("descripcion", String.class);
                                        LocalDate date_issue = row.get("date_issue", LocalDate.class);
                                        String url_comprobate = row.get("url_comprobate", String.class);
                                        return new MovementProject(movement_id, account_id, account_name, category_id,
                                                        category_name,
                                                        category_type, monto, descripcion, date_issue, url_comprobate);
                                }).all();
        }

        public Flux<MovementProject> findAllByUserIdAndCategoryId(UUID userId, UUID categoryId,
                        PaginationRequest request) {
                String column = SOIRT_COLUMN.getOrDefault(request.getSortBy(), "m.fecha_registro");

                String order = "asc".equalsIgnoreCase(request.getDirection()) ? "ASC" : "DESC";

                String sql = """
                                SELECT
                                        m.id AS movement_id,
                                        cu.id AS account_id,
                                        cu.nombre AS account_name,
                                        ca.id AS category_id,
                                        ca.nombre AS category_name,
                                        ca.tipo AS category_type,
                                        m.monto AS monto,
                                        m.descripcion AS descripcion,
                                        m.fecha_registro AS date_issue,
                                        m.url_comprobante AS url_comprobate
                                        FROM movimientos m
                                    INNER JOIN cuentas cu ON m.cuenta_id = cu.id
                                    INNER JOIN categorias ca ON m.categoria_id = ca.id
                                    WHERE m.usuario_id = :userId AND m.categoria_id = :categoryId
                                    ORDER BY %s %s, m.id
                                    LIMIT :size OFFSET :offset
                                """.formatted(column, order);

                return databaseClient.sql(sql)
                                .bind("userId", userId)
                                .bind("categoryId", categoryId)
                                .bind("size", request.getPageSize())
                                .bind("offset", (long) request.getPageNum() * request.getPageSize())
                                .map((row, metadata) -> {
                                        UUID movement_id = row.get("movement_id", UUID.class);
                                        UUID account_id = row.get("account_id", UUID.class);
                                        String account_name = row.get("account_name", String.class);
                                        UUID category_id = row.get("category_id", UUID.class);
                                        String category_name = row.get("category_name", String.class);
                                        String category_type = row.get("category_type", String.class);
                                        BigDecimal monto = row.get("monto", BigDecimal.class);
                                        String descripcion = row.get("descripcion", String.class);
                                        LocalDate date_issue = row.get("date_issue", LocalDate.class);
                                        String url_comprobate = row.get("url_comprobate", String.class);
                                        return new MovementProject(movement_id, account_id, account_name, category_id,
                                                        category_name,
                                                        category_type, monto, descripcion, date_issue, url_comprobate);
                                }).all();
        }

        public Flux<MovementsMonth> findByPeriod(UUID userId, Integer limit) {
                String sql = """
                                SELECT
                                	DATE_TRUNC('month', fecha_registro) AS periodo,
                                	SUM(
                                	CASE WHEN tipo = 'INGRESO' THEN monto
                                	ELSE 0
                                	END
                                	) AS suma_ingreso,
                                	SUM(
                                	CASE WHEN tipo = 'EGRESO' THEN monto
                                	ELSE 0
                                	END
                                	) AS suma_egreso
                                FROM movimientos WHERE usuario_id = :userId
                                GROUP BY DATE_TRUNC('month', fecha_registro)
                                ORDER BY periodo desc LIMIT :limit
                                                                """;
                return databaseClient.sql(sql)
                                .bind("userId", userId)
                                .bind("limit", limit)
                                .map((row, metadata) -> {
                                        LocalDate period = row.get("periodo", LocalDate.class);
                                        BigDecimal totalIncome = row.get("suma_ingreso", BigDecimal.class);
                                        BigDecimal totalExpencess = row.get("suma_egreso", BigDecimal.class);
                                        return new MovementsMonth(period, totalIncome, totalExpencess);
                                }).all();
        }

        public Flux<CategoriesExcenses> findExpensesByCategory(UUID userId, LocalDate from, LocalDate until) {
                String sql = """
                                SELECT
                                	m.categoria_id AS categoria_id,
                                	c.nombre AS nombre_categoria,
                                	DATE_TRUNC('month', m.fecha_registro) AS periodo,
                                	SUM(
                                	CASE WHEN m.tipo = 'EGRESO' THEN monto
                                	ELSE 0
                                	END
                                	) AS monto_categoria
                                FROM movimientos m
                                JOIN categorias c ON m.categoria_id= c.id
                                WHERE m.usuario_id = :userId
                                AND m.tipo = 'EGRESO'
                                AND m.fecha_registro BETWEEN :from AND :until
                                GROUP BY categoria_id, nombre_categoria, periodo
                                """;

                return databaseClient.sql(sql)
                                .bind("userId", userId)
                                .bind("from", from)
                                .bind("until", until)
                                .map((row, metadata) -> {
                                        UUID categoryId = row.get("categoria_id", UUID.class);
                                        String nameCategory = row.get("nombre_categoria", String.class);
                                        BigDecimal amount = row.get("monto_categoria", BigDecimal.class);

                                        return new CategoriesExcenses(categoryId, nameCategory, amount, null);
                                }).all();
        }
}
