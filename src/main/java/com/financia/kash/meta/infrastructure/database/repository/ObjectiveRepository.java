package com.financia.kash.meta.infrastructure.database.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.meta.infrastructure.database.entity.FinancialObjectiveEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ObjectiveRepository extends ReactiveCrudRepository<FinancialObjectiveEntity, UUID> {
    Flux<FinancialObjectiveEntity> findAllByUserId(UUID userId, Pageable pageable);

    Mono<Long> countByUserId(UUID userId);

    @Query("""
            SELECT EXISTS(
                SELECT 1
                FROM metas_financieras m
                LEFT JOIN usuarios u ON u.id = m.usuario_id
                WHERE m.id = :id
                AND u.email = :email
            )
                   """)
    Mono<Boolean> existsByIdAndOwnerEmail(UUID id, String email);
}
