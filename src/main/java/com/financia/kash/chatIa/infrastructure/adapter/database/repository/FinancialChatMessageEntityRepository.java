package com.financia.kash.chatIa.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.chatIa.infrastructure.adapter.database.entity.FinancialChatMessageEntity;

import reactor.core.publisher.Flux;

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
}
