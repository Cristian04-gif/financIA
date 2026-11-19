package com.financia.kash.chatIa.application.port.input;

import java.util.UUID;

import com.financia.kash.chatIa.domain.model.FinancialChatStreamResponse;

import reactor.core.publisher.Flux;

public interface SendFinancialChatMessageUseCase {
    Flux<FinancialChatStreamResponse> execute(UUID userId, UUID conversationId, String message);
}
