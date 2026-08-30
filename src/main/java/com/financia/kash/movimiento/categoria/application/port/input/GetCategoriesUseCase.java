package com.financia.kash.movimiento.categoria.application.port.input;

import java.util.List;
import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.Category;

public interface GetCategoriesUseCase {
    List<Category> getGlobalCategories();

    List<Category> getAllMyCategory(UUID userId);

    Category getById(UUID id);
}
