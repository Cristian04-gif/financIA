package com.financia.kash.categoria.application.service;

import java.util.List;
import java.util.UUID;

import com.financia.kash.categoria.application.port.input.ManageCategoriesUseCase;
import com.financia.kash.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.categoria.domain.model.Category;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CategoryService implements ManageCategoriesUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;

    @Override
    public Category save(Category category) {
        return categoryRepositoryPort.save(category);
    }

    @Override
    public Category findById(UUID id) {
        return categoryRepositoryPort.findById(id);
    }

    @Override
    public List<Category> findByUserId(UUID userId) {
        return categoryRepositoryPort.findByUserId(userId);
    }

    @Override
    public void delete(UUID id) {
        categoryRepositoryPort.delete(id);
    }
}
