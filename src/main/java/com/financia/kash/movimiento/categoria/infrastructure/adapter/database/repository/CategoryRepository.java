package com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.project.ProjectCategory;

import reactor.core.publisher.Flux;

public interface CategoryRepository extends ReactiveCrudRepository<CategoryEntity, UUID> {

    Flux<ProjectCategory> findByUserIdIsNullAndParentCategoryIdIsNull();

    Flux<ProjectCategory> findAllByUserId(UUID id);

}
