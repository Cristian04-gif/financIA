package com.financia.kash.movimiento.suscripcion.application.port.input;

import com.financia.kash.movimiento.suscripcion.application.port.input.command.CreateSubscriptionCommand;
import com.financia.kash.movimiento.suscripcion.domain.model.Subscription;

import reactor.core.publisher.Mono;

public interface CreateSubscriptionUseCase {
    Mono<Subscription> create(CreateSubscriptionCommand command);
}
