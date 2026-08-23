package com.financia.kash.movimiento.categoria.application.port.output;

import java.util.List;
import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.Category;

public interface CategoryRepositoryPort {
    List<Category> findAll();

    List<Category> findAllMyCategories(UUID userId);

    Category findById(UUID id);

    boolean existsById(UUID id);

    Category save(Category category);

    void delete(UUID id);
}
