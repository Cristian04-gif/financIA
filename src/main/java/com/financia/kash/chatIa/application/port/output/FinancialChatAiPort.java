package com.financia.kash.chatIa.application.port.output;

import com.financia.kash.chatIa.domain.model.FinancialChatContext;

import reactor.core.publisher.Flux;

public interface FinancialChatAiPort {
    Flux<String> generateResponse(FinancialChatContext context);

}
