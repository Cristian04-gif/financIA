package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.entity.MovementEntity;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.project.MovementProject;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementEntityRepository extends ReactiveCrudRepository<MovementEntity, UUID> {

    Flux<MovementEntity> findAllByUserIdAndCategoryIdAndCreationDateBetween(UUID userId, UUID categoryId,
            LocalDateTime from, LocalDateTime until);

    Mono<Long> countByUserId(UUID userId);

    Mono<Long> countByUserIdAndCategoryId(UUID userId, UUID categoryId);

    @Query("""
            SELECT
                m.id AS movement_id,
                cu.id AS account_id,
                cu.nombre AS account_name,
                ca.id AS category_id,
                ca.nombre AS category_name,
                ca.tipo AS category_type,
                m.monto AS monto,
                m.descripcion AS descripcion,
                m.fecha_registro AS date_issue
                FROM movimientos m
            INNER JOIN cuentas cu ON m.cuenta_id = cu.id
            INNER JOIN categorias ca ON m.categoria_id = ca.id
            WHERE m.id = :id
                        """)
    Mono<MovementProject> findByIdProject(UUID id);

    @Query("""
            SELECT EXISTS(
                SELECT 1
                FROM movimientos m
                LEFT JOIN usuarios u ON u.id = m.usuario_id
                WHERE m.id = :id
                AND u.email = :email
            )
                   """)
    Mono<Boolean> existsByIdAndOwnerEmail(UUID id, String email);
}
