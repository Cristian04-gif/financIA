package com.financia.kash.movimiento.suscripcion.application.port.input;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface ChangeStatusSubscriptionUseCase {
    Mono<Void> changeStatusSubscription(UUID subsId);
}
