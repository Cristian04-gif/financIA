package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.entity.MovementEntity;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.project.MovementProject;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementEntityRepository extends ReactiveCrudRepository<MovementEntity, UUID> {

    @Query("""
            SELECT
                m.id AS movement_id,
                cu.id AS account_id,
                cu.nombre AS account_name,
                ca.id AS category_id,
                ca.nombre AS category_name,
                ca.tipo AS category_type,
                m.monto AS amount,
                m.descripcion AS description,
                m.fecha_emision AS date_issue
                FROM movimientos m
            INNER JOIN cuentas cu ON m.cuenta_id = cu.id
            INNER JOIN categorias ca ON m.categoria_id = ca.id
            WHERE m.usuario_id = :userId
                        """)
    Flux<MovementProject> findAllByUserId(UUID userId, Pageable pageable);

    Mono<Long> countByUserId(UUID userId);

    @Query("""
            SELECT
                m.id AS movement_id,
                cu.id AS account_id,
                cu.nombre AS account_name,
                ca.id AS category_id,
                ca.nombre AS category_name,
                ca.tipo AS category_type,
                m.monto AS amount,
                m.descripcion AS description,
                m.fecha_emision AS date_issue
                FROM movimientos m
            INNER JOIN cuentas cu ON m.cuenta_id = cu.id
            INNER JOIN categorias ca ON m.categoria_id = ca.id
            WHERE m.id = :id
                        """)
    Mono<MovementProject> findByIdProject(UUID id);
}
