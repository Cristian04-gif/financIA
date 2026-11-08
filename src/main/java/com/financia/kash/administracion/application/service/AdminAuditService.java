package com.financia.kash.administracion.application.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import com.financia.kash.administracion.application.port.input.GetAdminAuditUseCase;
import com.financia.kash.administracion.application.port.output.AdminAuditPort;
import com.financia.kash.administracion.domain.model.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AdminAuditService implements GetAdminAuditUseCase {
    private final AdminAuditPort audit;

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Mono<AdminPage<AuditEntry>> get(UUID actor, AuditAction action, int page, int size) {
        return Mono.defer(() -> {
            AdminPage.validate(page, size);
            return audit.find(actor, action, page, size).collectList()
                    .flatMap(entries -> audit.count(actor, action)
                            .map(count -> AdminPage.of(entries, page, size, count)));
        });
    }
}
