package com.financia.kash.categoria.application.port.input;

import java.util.List;
import java.util.UUID;

import com.financia.kash.categoria.domain.model.Category;

public interface ManageCategoriesUseCase {
    Category save(Category category);

    Category findById(UUID id);

    List<Category> findByUserId(UUID userId);

    void delete(UUID id);
}
