package com.financia.kash.movimiento.categoria.application.port.output;

import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.Category;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepositoryPort {
    Flux<Category> findGlobalCategories();

    Flux<Category> findAllMyCategories(UUID userId);

    Mono<Category> findById(UUID id);

    Mono<Boolean> existsById(UUID id);

    Mono<Category> save(Category category);

    Mono<Void> delete(UUID id);
}
