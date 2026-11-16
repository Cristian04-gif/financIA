package com.financia.kash.administracion.infrastructure.database;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import com.financia.kash.administracion.application.port.output.AdminAuditPort;
import com.financia.kash.administracion.domain.model.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AdminAuditAdapter implements AdminAuditPort {
    private static final String FILTER = " WHERE (:actor = '' OR actor_id::text = :actor) "
            + "AND (:action = '' OR accion = :action)";
    private final DatabaseClient database;

    public Mono<Void> append(UUID actor, AuditAction action, UUID target, String detail) {
        return database.sql("INSERT INTO auditoria_administrativa(id, actor_id, accion, recurso_id, detalle) "
                + "VALUES(:id, :actor, :action, :target, :detail)")
                .bind("id", UUID.randomUUID()).bind("actor", actor).bind("action", action.name())
                .bind("target", target).bind("detail", detail).then();
    }

    public Flux<AuditEntry> find(UUID actor, AuditAction action, int page, int size) {
        return filtered("SELECT id, actor_id, accion, recurso_id, detalle, fecha "
                + "FROM auditoria_administrativa" + FILTER + " ORDER BY fecha DESC, id ASC LIMIT :size OFFSET :offset", actor, action)
                .bind("size", size).bind("offset", (long) page * size)
                .map((row, metadata) -> new AuditEntry(row.get("id", UUID.class), row.get("actor_id", UUID.class),
                        AuditAction.valueOf(row.get("accion", String.class)), row.get("recurso_id", UUID.class),
                        row.get("detalle", String.class), row.get("fecha", OffsetDateTime.class).toInstant())).all();
    }

    public Mono<Long> count(UUID actor, AuditAction action) {
        return filtered("SELECT COUNT(*) AS total FROM auditoria_administrativa" + FILTER, actor, action)
                .map((row, metadata) -> row.get("total", Long.class)).one();
    }

    private DatabaseClient.GenericExecuteSpec filtered(String sql, UUID actor, AuditAction action) {
        return database.sql(sql).bind("actor", actor == null ? "" : actor.toString())
                .bind("action", action == null ? "" : action.name());
    }
}
