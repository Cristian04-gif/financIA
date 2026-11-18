package com.financia.kash.chatIa.application.port.input;

import java.util.UUID;

import com.financia.kash.chatIa.domain.model.FinancialConversation;

import reactor.core.publisher.Mono;

public interface CreateFinancialConversationUseCase {
    Mono<FinancialConversation> execute(UUID userId);

}
