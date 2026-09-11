package com.financia.kash.movimiento.categoria.application.port.output;

import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

import reactor.core.publisher.Mono;

public interface UserForCategoryPort {
    Mono<User> findUserById(UUID userI);
}
