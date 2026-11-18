package com.financia.kash.chatIa.application.port.output;

import java.util.UUID;

import com.financia.kash.chatIa.domain.model.FinancialChatMessage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FinancialChatMessageRepositoryPort {
    Mono<FinancialChatMessage> save(
            FinancialChatMessage message);

    Flux<FinancialChatMessage> findAllByConversationId(
            UUID conversationId);

    Flux<FinancialChatMessage> findRecentByConversationId(
            UUID conversationId,
            int limit);
}
