package com.financia.kash.movimiento.categoria.infrastructure.adapter.database;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.meta.application.port.output.CategoryForObjectivesPort;
import com.financia.kash.movimiento.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.movimiento.categoria.domain.exception.CategoryNotFoundException;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.mapping.CategoryMapper;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryEntityRepository;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryentityQueryRepository;
import com.financia.kash.movimiento.comprobante.application.port.output.CategoryForVoucherPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter
        implements CategoryRepositoryPort, CategoryForVoucherPort, CategoryForObjectivesPort {

    private final CategoryEntityRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryentityQueryRepository categoryentityQueryRepository;

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
        return categoryRepository.findById(id).switchIfEmpty(Mono.error(new CategoryNotFoundException(id)))
                .map(categoryMapper::mapToDomain);
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

    @Override
    public Flux<Category> findCategoryForVoucher(UUID userId, String name) {
        return categoryentityQueryRepository.findCategoryForVoucher(userId, name).map(categoryMapper::mapToDomain);
    }

}
