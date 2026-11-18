package com.financia.kash.chatIa.domain.exception;

import java.util.UUID;

public class ConversarionNotFoundException extends RuntimeException {

    public ConversarionNotFoundException(UUID id) {
        super("No se encontro la conversacion %s".formatted(id));
    }

    public ConversarionNotFoundException(UUID id, UUID userId) {
        super("No se encontro la conversacion %s del usuario %s".formatted(id, userId));
    }

}
