package com.financia.kash.chatIa.application.port.input;

import java.util.UUID;

import com.financia.kash.chatIa.domain.model.FinancialConversation;

import reactor.core.publisher.Flux;

public interface GetFinancialConversationsUseCase {
    Flux<FinancialConversation> execute(UUID userId);

}
