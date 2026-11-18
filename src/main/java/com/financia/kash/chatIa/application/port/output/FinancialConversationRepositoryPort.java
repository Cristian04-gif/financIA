package com.financia.kash.chatIa.application.port.output;

import java.util.UUID;

import com.financia.kash.chatIa.domain.model.FinancialConversation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FinancialConversationRepositoryPort {
    Mono<FinancialConversation> save(
            FinancialConversation conversation);

    Mono<FinancialConversation> findByIdAndUserId(
            UUID conversationId,
            UUID userId);

    Flux<FinancialConversation> findAllByUserId(
            UUID userId);

    Mono<Void> update(FinancialConversation conversation);
}
