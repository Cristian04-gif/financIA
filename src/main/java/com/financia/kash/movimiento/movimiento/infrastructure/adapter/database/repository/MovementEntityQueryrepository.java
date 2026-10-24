package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

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
}
