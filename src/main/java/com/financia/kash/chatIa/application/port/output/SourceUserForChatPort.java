package com.financia.kash.chatIa.application.port.output;

import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

import reactor.core.publisher.Mono;

public interface SourceUserForChatPort {
    Mono<User> findMe(UUID id);
}
