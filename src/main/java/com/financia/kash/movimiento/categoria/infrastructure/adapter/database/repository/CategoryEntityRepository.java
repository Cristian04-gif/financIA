package com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.project.ProjectCategory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryEntityRepository extends ReactiveCrudRepository<CategoryEntity, UUID> {

    @Query("UPDATE categorias SET nombre = :name, fecha_actualizacion = CURRENT_DATE "
            + "WHERE id = :id AND usuario_id IS NULL AND categoria_padre_id IS NULL RETURNING *")
    Mono<CategoryEntity> renameGlobal(UUID id, String name);

    @Query("UPDATE categorias SET activo = :active, fecha_actualizacion = CURRENT_DATE "
            + "WHERE id = :id AND usuario_id IS NULL AND categoria_padre_id IS NULL RETURNING *")
    Mono<CategoryEntity> changeGlobalStatus(UUID id, boolean active);

    @Query("SELECT EXISTS(SELECT 1 FROM categorias c JOIN usuarios u ON u.id = c.usuario_id "
            + "WHERE c.id = :id AND u.email = :email)")
    Mono<Boolean> existsOwnedByIdAndEmail(UUID id, String email);

    Flux<ProjectCategory> findByUserIdIsNullAndParentCategoryIdIsNull();

    Flux<ProjectCategory> findAllByUserIdOrParentCategoryIdIsNull(UUID userId);

    Flux<ProjectCategory> findByUserIdAndName(UUID userId, String name);;

    Flux<ProjectCategory> findAllByUserId(UUID id);

    @Query("""
                    SELECT EXISTS(
            SELECT 1
            FROM categorias c
            LEFT JOIN usuarios u ON u.id = c.usuario_id
            WHERE c.id = :id
             AND (u.email = :email OR c.usuario_id IS NULL)
                   )
                   """)
    Mono<Boolean> existsByIdAndOwnerEmail(UUID id, String email);

}
