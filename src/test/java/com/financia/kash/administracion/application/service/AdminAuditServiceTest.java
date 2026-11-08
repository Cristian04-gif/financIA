package com.financia.kash.administracion.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import com.financia.kash.administracion.application.port.output.AdminAuditPort;
import com.financia.kash.administracion.domain.model.*;
import reactor.core.publisher.*;

class AdminAuditServiceTest {
    @Test
    void auditPaginationIsBoundedAndPreservesFilters() {
        var port = mock(AdminAuditPort.class);
        var service = new AdminAuditService(port);
        UUID actor = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> service.get(actor, null, 0, 101).block());
        verifyNoInteractions(port);
        var entry = new AuditEntry(UUID.randomUUID(), actor, AuditAction.USER_STATUS_CHANGED,
                UUID.randomUUID(), "SUSPENDIDO: Motivo", Instant.now());
        when(port.find(actor, AuditAction.USER_STATUS_CHANGED, 0, 20)).thenReturn(Flux.just(entry));
        when(port.count(actor, AuditAction.USER_STATUS_CHANGED)).thenReturn(Mono.just(1L));
        var page = service.get(actor, AuditAction.USER_STATUS_CHANGED, 0, 20).block();
        assertEquals(1, page.totalElements());
        assertEquals(entry, page.content().getFirst());
    }
}
