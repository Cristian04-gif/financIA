package com.financia.kash.administracion.application.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.financia.kash.administracion.application.port.output.AdminAuditPort;
import com.financia.kash.administracion.domain.model.AuditAction;
import com.financia.kash.administracion.application.port.input.ManageGlobalCategoriesUseCase;
import com.financia.kash.administracion.application.port.output.GlobalCategoryPort;
import com.financia.kash.movimiento.categoria.domain.enums.CategoryType;
import com.financia.kash.movimiento.categoria.domain.exception.CategoryNotFoundException;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GlobalCategoryAdminService implements ManageGlobalCategoriesUseCase {
    private final GlobalCategoryPort categories;
    private final AdminAuditPort audit;

    public Flux<Category> list() { return categories.findAll(); }

    @Transactional
    public Mono<Category> create(UUID actorId, String name, CategoryType type) {
        return Mono.defer(() -> categories.create(new Category(validName(name), type.name())))
                .flatMap(saved -> audit.append(actorId, AuditAction.CATEGORY_CREATED, saved.getId(),
                        saved.getType().name() + ": " + saved.getName()).thenReturn(saved))
                .flatMap(this::invalidate);
    }

    @Transactional
    public Mono<Category> rename(UUID actorId, UUID id, String name) {
        return Mono.defer(() -> categories.rename(id, validName(name)))
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .flatMap(saved -> audit.append(actorId, AuditAction.CATEGORY_RENAMED, id,
                        saved.getName()).thenReturn(saved)).flatMap(this::invalidate);
    }

    @Transactional
    public Mono<Category> changeStatus(UUID actorId, UUID id, boolean active) {
        return categories.changeStatus(id, active)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .flatMap(saved -> audit.append(actorId, AuditAction.CATEGORY_STATUS_CHANGED, id,
                        "active=" + active).thenReturn(saved)).flatMap(this::invalidate);
    }

    private Mono<Category> invalidate(Category category) {
        return categories.invalidateCache(category.getId()).thenReturn(category);
    }

    private String validName(String name) {
        if (name == null || name.isBlank() || name.strip().length() > 255) {
            throw new IllegalArgumentException("El nombre es obligatorio y admite hasta 255 caracteres");
        }
        return name.strip();
    }
}
