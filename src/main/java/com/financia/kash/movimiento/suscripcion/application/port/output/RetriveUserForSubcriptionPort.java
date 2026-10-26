package com.financia.kash.movimiento.suscripcion.application.port.output;

import java.util.UUID;

import com.financia.kash.usuario.domain.model.User;

import reactor.core.publisher.Mono;

public interface RetriveUserForSubcriptionPort {

    Mono<User> findMe(UUID userId);
}
