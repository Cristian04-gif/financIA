package com.financia.kash.categoria.application.service;

import java.util.List;
import java.util.UUID;

import com.financia.kash.categoria.application.port.input.ManageCategoriesUseCase;
import com.financia.kash.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.categoria.domain.model.Category;
import com.financia.kash.categoria.domain.model.CategoryType;

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
    public Category rename(UUID id, String name) {

        Category category = categoryRepositoryPort.findById(id);

        category.rename(name);

        return categoryRepositoryPort.save(category);
    }

    @Override
    public Category changeType(UUID id, CategoryType type) {

        Category category = categoryRepositoryPort.findById(id);

        category.changeType(type);

        return categoryRepositoryPort.save(category);
    }

    @Override
    public void deactivate(UUID id) {

        Category category = categoryRepositoryPort.findById(id);

        category.deactivate();

        categoryRepositoryPort.save(category);
    }
}