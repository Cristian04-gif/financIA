package com.financia.kash.administracion.application.port.output;

import java.util.UUID;
import com.financia.kash.administracion.domain.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminAuditPort {
    Mono<Void> append(UUID actor, AuditAction action, UUID target, String detail);
    Flux<AuditEntry> find(UUID actor, AuditAction action, int page, int size);
    Mono<Long> count(UUID actor, AuditAction action);
}
