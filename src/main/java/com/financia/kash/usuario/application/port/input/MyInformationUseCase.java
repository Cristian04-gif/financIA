package com.financia.kash.usuario.application.port.input;

import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

import reactor.core.publisher.Mono;

public interface MyInformationUseCase {
    Mono<User> findMe(UUID id);

    Mono<User> findMe(String email);
}
