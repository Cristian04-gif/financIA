package com.financia.kash.chatIa.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record FinancialChatStreamResponse(
        String type,
        UUID conversationId,
        UUID messageId,
        String content,
        LocalDateTime creationDate) {

}
