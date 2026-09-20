package com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.project.ProjectCategory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryEntityRepository extends ReactiveCrudRepository<CategoryEntity, UUID> {

    Flux<ProjectCategory> findByUserIdIsNullAndParentCategoryIdIsNull();

    Flux<ProjectCategory> findAllByUserId(UUID id);

    @Query("""
            SELECT EXISTS(
                SELECT 1
                FROM categorias c
                INNER JOIN usuarios u ON u.id = c.usuario_id
                WHERE c.id = :id
                AND u.email = :email
            )
            """)
    Mono<Boolean> existsByIdAndOwnerEmail(UUID id, String email);

}
