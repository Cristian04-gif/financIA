package com.financia.kash.movimiento.categoria.infrastructure.adapter.database;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.movimiento.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.movimiento.categoria.domain.exception.CategoryNotFoundException;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.mapping.CategoryMapper;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Flux<Category> findGlobalCategories() {
        return categoryRepository.findByUserIdIsNullAndParentCategoryIdIsNull()
                .map(categoryMapper::mapToDomain);
    }

    @Override
    public Flux<Category> findAllMyCategories(UUID userId) {
        return categoryRepository.findAllByUserId(userId).map(categoryMapper::mapToDomain);
    }

    @Override
    public Mono<Category> findById(UUID id) {
        return categoryRepository.findById(id).map(categoryMapper::mapToDomain)
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(id)));
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public Mono<Category> save(Category category) {
        return Mono.just(categoryMapper.mapToEntity(category)).flatMap(entity -> categoryRepository.save(entity))
                .map(categoryMapper::mapToDomain);
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return categoryRepository.deleteById(id);
    }

}
