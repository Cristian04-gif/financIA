package com.financia.kash.movimiento.categoria.infrastructure.adapter.database;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.movimiento.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.movimiento.categoria.domain.exception.CategoryNotFoundException;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.mapping.CategoryMapper;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> findGlobalCategories() {
        return categoryRepository.findByUserIsNullAndParentCategoryIdIsNull().stream()
                .map(categoryMapper::mapToDomain).toList();
    }

    @Override
    public List<Category> findAllMyCategories(UUID userId) {
        return categoryRepository.findAllByUserId(userId).stream().map(categoryMapper::mapToDomain).toList();
    }

    @Override
    public Category findById(UUID id) {
        return categoryRepository.findById(id).map(categoryMapper::mapToDomain)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public boolean existsById(UUID id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity categoryEntity = categoryMapper.mapToEntity(category);
        CategoryEntity saved = categoryRepository.save(categoryEntity);
        return categoryMapper.mapToDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        categoryRepository.deleteById(id);
    }

}
