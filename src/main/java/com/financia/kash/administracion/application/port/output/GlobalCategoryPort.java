package com.financia.kash.administracion.application.port.output;

import java.util.UUID;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GlobalCategoryPort {
    Flux<Category> findAll();
    Mono<Category> create(Category category);
    Mono<Category> rename(UUID id, String name);
    Mono<Category> changeStatus(UUID id, boolean active);
    Mono<Void> invalidateCache(UUID id);
}
