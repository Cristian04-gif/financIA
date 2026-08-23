package com.financia.kash.movimiento.categoria.application.port.input;

import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.Category;

public interface CreateCategoryUseCase {
    Category createMainCategory(String name, String type);

    Category createCategoryForUser(UUID userId, String name, String type, UUID parentCategoryId);
}
