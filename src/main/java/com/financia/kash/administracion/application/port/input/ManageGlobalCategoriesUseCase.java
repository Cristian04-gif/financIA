package com.financia.kash.administracion.application.port.input;

import java.util.UUID;
import com.financia.kash.movimiento.categoria.domain.enums.CategoryType;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageGlobalCategoriesUseCase {
    Flux<Category> list();
    Mono<Category> create(UUID actorId, String name, CategoryType type);
    Mono<Category> rename(UUID actorId, UUID id, String name);
    Mono<Category> changeStatus(UUID actorId, UUID id, boolean active);
}
