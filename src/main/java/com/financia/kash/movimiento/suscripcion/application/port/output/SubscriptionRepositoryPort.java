package com.financia.kash.movimiento.suscripcion.application.port.output;

import java.util.UUID;

import com.financia.kash.movimiento.suscripcion.domain.model.Subscription;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubscriptionRepositoryPort {

    Flux<Subscription> findAllByUser(UUID userId);

    Mono<Subscription> findById(UUID subsId);

    Mono<Subscription> save(Subscription subscription);

}
