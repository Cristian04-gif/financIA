package com.financia.kash.categoria.application.port.input;

import java.util.List;
import java.util.UUID;

import com.financia.kash.categoria.domain.model.Category;
import com.financia.kash.categoria.domain.model.CategoryType;

public interface ManageCategoriesUseCase {

    Category save(Category category);

    Category findById(UUID id);

    List<Category> findByUserId(UUID userId);

    Category rename(UUID id, String name);

    Category changeType(UUID id, CategoryType type);

    void deactivate(UUID id);
}
