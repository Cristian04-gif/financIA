package com.financia.kash.movimiento.suscripcion.application.port.input;

import reactor.core.publisher.Mono;

public interface SubscriptionDiscountuseCase {

    Mono<Void> subscriptionDiscount();
}
