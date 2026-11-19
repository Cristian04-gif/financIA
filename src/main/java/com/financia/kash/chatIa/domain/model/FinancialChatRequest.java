package com.financia.kash.chatIa.domain.model;

import java.util.UUID;

public record FinancialChatRequest(UUID conversationId,
        String message) {

}
