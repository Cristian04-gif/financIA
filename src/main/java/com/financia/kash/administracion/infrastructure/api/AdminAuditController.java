package com.financia.kash.administracion.infrastructure.api;

import java.util.UUID;
import org.springframework.web.bind.annotation.*;
import com.financia.kash.administracion.application.port.input.GetAdminAuditUseCase;
import com.financia.kash.administracion.domain.model.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
@Tag(name = "Administracion - Auditoria")
public class AdminAuditController {
    private final GetAdminAuditUseCase audit;

    @GetMapping
    public Mono<AdminPage<AuditEntry>> get(@RequestParam(required = false) UUID actorId,
            @RequestParam(required = false) AuditAction action, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return audit.get(actorId, action, page, size);
    }
}
