package com.financia.kash.movimiento.categoria.application.port.input;

import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.Category;

import reactor.core.publisher.Mono;

public interface UpdateCategoryUseCase {
    Mono<Category> updateCategoryForUser(UUID id, String name, String type, UUID parentId, boolean active);

}
