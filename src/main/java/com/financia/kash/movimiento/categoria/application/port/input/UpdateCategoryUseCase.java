package com.financia.kash.movimiento.categoria.application.port.input;

import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.Category;

public interface UpdateCategoryUseCase {
    Category updateCategoryForUser(UUID id, String name, String type, UUID parentId, boolean active);

}
