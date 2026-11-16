package com.financia.kash.administracion.application.port.input;

import java.util.UUID;
import com.financia.kash.administracion.domain.model.*;
import reactor.core.publisher.Mono;

public interface GetAdminAuditUseCase {
    Mono<AdminPage<AuditEntry>> get(UUID actor, AuditAction action, int page, int size);
}
