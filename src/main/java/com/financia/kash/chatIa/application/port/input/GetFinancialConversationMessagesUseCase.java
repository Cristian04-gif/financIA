package com.financia.kash.chatIa.application.port.input;

import java.util.UUID;

import com.financia.kash.chatIa.domain.model.FinancialChatMessage;

import reactor.core.publisher.Flux;

public interface GetFinancialConversationMessagesUseCase {
    Flux<FinancialChatMessage> execute(UUID conversationId, UUID userId);
}
