package com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.entity.SubscriptionEntity;
import com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.repository.project.SubscriptionProject;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface SubscriptionEntityRepository extends ReactiveCrudRepository<SubscriptionEntity, UUID> {

    Flux<SubscriptionProject> findAllByUserId(UUID userId);

    Flux<SubscriptionEntity> findByDateNextPayment(LocalDate dateNextPayment);

    @Query("""
            SELECT EXISTS(
                SELECT 1
                FROM suscripciones s
                LEFT JOIN usuarios u ON u.id = s.usuario_id
                WHERE s.id = :id
                AND u.email = :email
            )
                   """)
    Mono<Boolean> existsByIdAndOwnerEmail(UUID id, String email);

}
