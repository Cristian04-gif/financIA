package com.financia.kash.chatIa.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record FinancialConversation(
        UUID id,
        UUID userId,
        String title,
        Boolean active,
        LocalDateTime creationDate,
        LocalDateTime updateDate) {

    public static FinancialConversation create(UUID userId) {
        LocalDateTime now = LocalDateTime.now();

        return new FinancialConversation(
                UUID.randomUUID(),
                userId,
                "Nueva conversación",
                true,
                now,
                now);
    }
}
