package com.financia.kash.auth.application.port.output;

import com.financia.kash.usuario.domain.model.User;

import reactor.core.publisher.Mono;

public interface UserSaveForAuthPort {
    Mono<User> save(User user);
}
