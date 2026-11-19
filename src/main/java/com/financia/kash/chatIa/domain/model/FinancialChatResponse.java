package com.financia.kash.chatIa.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.chatIa.domain.enums.MessageType;

public record FinancialChatResponse(
        UUID conversationId,
        UUID messageId,
        MessageType type,
        String content,
        LocalDateTime creationDate) {

}
