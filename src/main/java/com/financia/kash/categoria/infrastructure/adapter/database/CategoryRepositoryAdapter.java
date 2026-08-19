package com.financia.kash.categoria.infrastructure.adapter.database;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.categoria.domain.model.Category;
import com.financia.kash.categoria.infrastructure.adapter.database.mapping.CategoryMapper;
import com.financia.kash.categoria.infrastructure.adapter.database.repository.CategoryEntityRepository;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryEntityRepository categoryEntityRepository;
    private final CategoryMapper categoryMapper;
    private final EntityManager entityManager;

    @Override
    public Category save(Category category) {
        UserEntity user = category.getUserId() == null ? null : entityManager.getReference(UserEntity.class, category.getUserId());
        return categoryMapper.mapToDomain(categoryEntityRepository.save(categoryMapper.mapToEntity(category, user)));
    }

    @Override
    public Category findById(UUID id) {
        return categoryEntityRepository.findById(id)
                .map(categoryMapper::mapToDomain)
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada: " + id));
    }

    @Override
    public List<Category> findByUserId(UUID userId) {
        return categoryEntityRepository.findAllByUser_IdOrDefaultCategoryTrue(userId).stream()
                .map(categoryMapper::mapToDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        categoryEntityRepository.deleteById(id);
    }
}
