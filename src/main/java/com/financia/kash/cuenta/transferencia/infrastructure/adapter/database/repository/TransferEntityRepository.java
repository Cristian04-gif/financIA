package com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.entity.TransferEntity;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.repository.project.TransferProject;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransferEntityRepository extends ReactiveCrudRepository<TransferEntity, UUID> {

    Flux<TransferEntity> findAllByUserId(UUID userId, Pageable pageable);

    @Query("""
                SELECT t.id,
                       t.cuenta_destino_id AS cuenta_destino_id,
                       s.nombre AS nombre,
                       t.descripcion AS descripcion,
                       t.fecha_creacion AS fecha_creacion,
                       t.monto AS monto
                FROM cuentas c
                INNER JOIN transferencias t ON c.id = t.cuenta_origen_id
                INNER JOIN cuentas s ON s.id = t.cuenta_destino_id
                WHERE c.id = :accountId
            """)
    Flux<TransferProject> findAllByAccountId(UUID accountId, Pageable pageable);

    Mono<Long> countBySourceAccount(UUID accountId);

    @Query("""
            SELECT EXISTS(
                    SELECT 1
                    FROM transferencias t
                    INNER JOIN usuarios u ON u.id = t.usuario_id
                    WHERE t.id = :id
                    AND u.email = :email
            )
                """)
    Mono<Boolean> existsByIdAndOwnerEmail(UUID id, String email);
}
