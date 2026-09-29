package com.financia.kash.movimiento.suscripcion.application.port.input;

import java.math.BigDecimal;
import java.util.UUID;

import com.financia.kash.movimiento.suscripcion.domain.model.Subscription;
import com.financia.kash.movimiento.suscripcion.domain.model.SubscriptionFrequency;

import reactor.core.publisher.Mono;

public interface CreateSubscriptionUseCase {
    Mono<Subscription> create(UUID userId, String name, BigDecimal amount, SubscriptionFrequency frequency,
            Integer payDay);
}
