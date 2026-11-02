package com.financia.kash.movimiento.suscripcion.application.port.input;

import java.util.UUID;

import com.financia.kash.movimiento.suscripcion.domain.model.Subscription;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetSubscriptionUseCase {
    Flux<Subscription> getSubscriptionForUser(UUID userId);

    Mono<Subscription> getById(UUID subsId);
}
