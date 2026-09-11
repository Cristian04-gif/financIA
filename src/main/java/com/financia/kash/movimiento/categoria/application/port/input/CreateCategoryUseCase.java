package com.financia.kash.movimiento.categoria.application.port.input;

import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.Category;

import reactor.core.publisher.Mono;

public interface CreateCategoryUseCase {
    Mono<Category> createMainCategory(String name, String type);

    Mono<Category> createCategoryForUser(UUID userId, String name, String type, UUID parentCategoryId);
}
