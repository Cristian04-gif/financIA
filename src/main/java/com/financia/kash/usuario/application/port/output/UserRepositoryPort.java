package com.financia.kash.usuario.application.port.output;

import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    Mono<User> getMe(UUID id);

    Mono<User> getMe(String email);

    Mono<Boolean> existEmail(String email);

    Mono<User> save(User user);

    Mono<Void> delete(UUID id);

}
