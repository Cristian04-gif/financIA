package com.financia.kash.chatIa.infrastructure.adapter.database.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.chatIa.infrastructure.adapter.database.entity.FinancialChatMessageEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FinancialChatMessageEntityRepository extends ReactiveCrudRepository<FinancialChatMessageEntity, UUID> {
        Flux<FinancialChatMessageEntity> findAllByConversationId(UUID conversationId);

        @Query("""
                        SELECT *
                        FROM mensajes_chat
                        WHERE conversacion_id = :conversationId
                        ORDER BY fecha_creacion DESC
                        LIMIT :limit
                        """)
        Flux<FinancialChatMessageEntity> findRecentByConversationId(
                        UUID conversationId,
                        int limit);

        @Query("""
                        SELECT COUNT(*)
                        FROM mensajes_chat m
                        INNER JOIN conversaciones_chat c
                            ON c.id = m.conversacion_id
                        WHERE c.usuario_id = :userId
                          AND m.tipo = 'USER'
                          AND m.fecha_creacion >= :startOfDay
                        """)
        Mono<Long> countUserMessagesByUserIdSince(
                        UUID userId,
                        LocalDateTime startOfDay);
}
