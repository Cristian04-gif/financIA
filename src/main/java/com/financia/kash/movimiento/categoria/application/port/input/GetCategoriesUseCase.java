package com.financia.kash.movimiento.categoria.application.port.input;

import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.Category;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetCategoriesUseCase {
    Flux<Category> getGlobalCategories();

    Flux<Category> getAllMyCategory(UUID userId);

    Mono<Category> getById(UUID id);
}
