package com.financia.kash.chatIa.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.chatIa.domain.enums.MessageType;

public record FinancialChatMessage(
        UUID id,
        UUID conversationId,
        MessageType type,
        String content,
        LocalDateTime creationDate) {
    public static FinancialChatMessage create(
            UUID conversationId,
            MessageType type,
            String content) {

        return new FinancialChatMessage(
                UUID.randomUUID(),
                conversationId,
                type,
                content,
                LocalDateTime.now());
    }
}
