package com.financia.kash.administracion.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AuditEntry(UUID id, UUID actorId, AuditAction action, UUID targetId,
        String detail, Instant occurredAt) {}
