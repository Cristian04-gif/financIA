package com.financia.kash.shared.application.port.output;

import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

import reactor.core.publisher.Mono;

public interface UserForSharedPort {
    Mono<User> findById(UUID id);
}
