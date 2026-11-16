package com.financia.kash.administracion.infrastructure.database;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import com.financia.kash.administracion.application.port.output.AdminUserPort;
import com.financia.kash.administracion.domain.model.AdminUser;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;
import com.financia.kash.usuario.domain.enums.*;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AdminUserAdapter implements AdminUserPort {
    private static final String FIELDS = "id, nombre, apellido, email, rol::text AS rol, estado::text AS estado, fecha_creacion, fecha_actualizacion";
    private static final String FILTER = " WHERE (:status = '' OR estado::text = :status) "
            + "AND POSITION(LOWER(:search) IN LOWER(nombre || ' ' || apellido || ' ' || email)) > 0";
    private final DatabaseClient database;
    private final ReactiveStringRedisTemplate redis;

    public Flux<AdminUser> search(String search, UserStatus status, int page, int size) {
        return filtered("SELECT " + FIELDS + " FROM usuarios" + FILTER
                + " ORDER BY fecha_creacion DESC, id ASC LIMIT :size OFFSET :offset", search, status)
                .bind("size", size).bind("offset", (long) page * size).map((row, metadata) -> map(row)).all();
    }

    public Mono<Long> count(String search, UserStatus status) {
        return filtered("SELECT COUNT(*) AS total FROM usuarios" + FILTER, search, status)
                .map((row, metadata) -> row.get("total", Long.class)).one();
    }

    private DatabaseClient.GenericExecuteSpec filtered(String sql, String search, UserStatus status) {
        return database.sql(sql).bind("search", search).bind("status", status == null ? "" : status.name());
    }

    public Mono<AdminUser> find(UUID id) {
        return database.sql("SELECT " + FIELDS + " FROM usuarios WHERE id = :id").bind("id", id)
                .map((row, metadata) -> map(row)).one();
    }

    public Mono<AdminUser> changeStatus(UUID id, UserStatus status) {
        return database.sql("UPDATE usuarios SET estado = CAST(:status AS estado_usuario), fecha_actualizacion = CURRENT_DATE "
                + "WHERE id = :id AND rol = 'USER' AND estado IN ('ACTIVO', 'SUSPENDIDO') RETURNING " + FIELDS)
                .bind("id", id).bind("status", status.name()).map((row, metadata) -> map(row)).one();
    }

    public Mono<Void> invalidateCache(AdminUser user) {
        return CacheKeys.clearAllCacheUser(redis, user.id(), user.email()).then();
    }

    private AdminUser map(Row row) {
        return new AdminUser(row.get("id", UUID.class), row.get("nombre", String.class),
                row.get("apellido", String.class), row.get("email", String.class),
                RoleUser.valueOf(row.get("rol", String.class)), UserStatus.valueOf(row.get("estado", String.class)),
                row.get("fecha_creacion", LocalDate.class), row.get("fecha_actualizacion", LocalDate.class));
    }
}
