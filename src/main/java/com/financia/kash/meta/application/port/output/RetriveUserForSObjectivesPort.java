package com.financia.kash.meta.application.port.output;

import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

import reactor.core.publisher.Mono;

public interface RetriveUserForSObjectivesPort {
    Mono<User> findMe(UUID userId);
}
