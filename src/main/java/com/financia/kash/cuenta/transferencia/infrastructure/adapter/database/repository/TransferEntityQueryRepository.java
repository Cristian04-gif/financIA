package com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.repository.project.TransferProject;
import com.financia.kash.shared.domain.PaginationRequest;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@Repository
@AllArgsConstructor
public class TransferEntityQueryRepository {

    private final DatabaseClient databaseClient;

    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "fecha_creacion", "t.fecha_creacion",
            "monto", "t.monto",
            "id", "t.id");

    public Flux<TransferProject> findByUser(UUID userId, PaginationRequest request) {
        String column = SORT_COLUMNS.getOrDefault(request.getSortBy(), "t.fecha_creacion");

        String order = "asc".equalsIgnoreCase(request.getDirection()) ? "ASC" : "DESC";

        String sql = """
                SELECT t.id AS id,
                       t.cuenta_origen_id AS cuenta_origen_id,
                       c.nombre AS nombre_o,
                       t.cuenta_destino_id AS cuenta_destino_id,
                       s.nombre AS nombre_d,
                       t.descripcion AS descripcion,
                       t.fecha_creacion AS fecha_creacion,
                       t.monto AS monto
                FROM transferencias t
                INNER JOIN cuentas c
                    ON c.id = t.cuenta_origen_id
                INNER JOIN cuentas s
                    ON s.id = t.cuenta_destino_id
                WHERE t.usuario_id = :userId
                ORDER BY %s %s, t.id DESC
                LIMIT :size OFFSET :offset
                """.formatted(column, order);

        return databaseClient.sql(sql)
                .bind("userId", userId)
                .bind("size", request.getPageSize())
                .bind("offset", (long) request.getPageNum() * request.getPageSize())
                .map((row, metadata) -> {
                    UUID id = row.get("id", UUID.class);
                    UUID cuenta_origen_id = row.get("cuenta_origen_id", UUID.class);
                    String nombre_o = row.get("nombre_o", String.class);
                    UUID cuenta_destino_id = row.get("cuenta_destino_id", UUID.class);
                    String nombre_d = row.get("nombre_d", String.class);
                    String descripcion = row.get("descripcion", String.class);
                    LocalDate fecha_creacion = row.get("fecha_creacion", LocalDate.class);
                    BigDecimal monto = row.get("monto", BigDecimal.class);
                    return new TransferProject(id, cuenta_origen_id, nombre_o, cuenta_destino_id, nombre_d, descripcion,
                            fecha_creacion, monto);
                })
                .all();
    }

    public Flux<TransferProject> findByAccount(UUID accountId, PaginationRequest request) {
        String column = SORT_COLUMNS.getOrDefault(request.getSortBy(), "t.fecha_creacion");

        String order = "asc".equalsIgnoreCase(request.getDirection()) ? "ASC" : "DESC";

        String sql = """
                SELECT t.id,
                       t.cuenta_destino_id AS cuenta_destino_id,
                       s.nombre AS nombre_d,
                       t.descripcion AS descripcion,
                       t.fecha_creacion AS fecha_creacion,
                       t.monto AS monto
                FROM cuentas c
                INNER JOIN transferencias t ON c.id = t.cuenta_origen_id
                INNER JOIN cuentas s ON s.id = t.cuenta_destino_id
                WHERE c.id = :accountId
                ORDER BY %s %s, t.id DESC
                LIMIT :size OFFSET :offset
                """.formatted(column, order);

        return databaseClient.sql(sql)
                .bind("accountId", accountId)
                .bind("size", request.getPageSize())
                .bind("offset", (long) request.getPageNum() * request.getPageSize())
                .map((row, metadata) -> {
                    UUID id = row.get("id", UUID.class);

                    UUID cuenta_destino_id = row.get("cuenta_destino_id", UUID.class);
                    String nombre_d = row.get("nombre_d", String.class);
                    String descripcion = row.get("descripcion", String.class);
                    LocalDate fecha_creacion = row.get("fecha_creacion", LocalDate.class);
                    BigDecimal monto = row.get("monto", BigDecimal.class);
                    return new TransferProject(id, null, null, cuenta_destino_id, nombre_d, descripcion,
                            fecha_creacion, monto);
                })
                .all();
    }
}
