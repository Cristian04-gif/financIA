package com.financia.kash.movimiento.suscripcion.application.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.movimiento.suscripcion.application.port.input.ChangeStatusSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.CreateSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.GetSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.output.SubscriptionRepositoryPort;
import com.financia.kash.movimiento.suscripcion.domain.model.Subscription;
import com.financia.kash.movimiento.suscripcion.domain.model.SubscriptionFrequency;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SubscriptionService
        implements GetSubscriptionUseCase, CreateSubscriptionUseCase, ChangeStatusSubscriptionUseCase {

    private final SubscriptionRepositoryPort subscriptionRepositoryPort;

    @Override
    public Flux<Subscription> getSubscriptionForUser(UUID userId) {
        return subscriptionRepositoryPort.findAllByUser(userId);
    }

    @Override
    public Mono<Subscription> getById(UUID subsId) {
        return subscriptionRepositoryPort.findById(subsId);
    }

    @Override
    public Mono<Subscription> create(UUID userId, String name, BigDecimal amount, SubscriptionFrequency frequency,
            Integer payDay) {
        return subscriptionRepositoryPort.save(Subscription.create(userId, name, amount, frequency, payDay));
    }

    @Override
    public Mono<Void> changeStatusSubscription(UUID subsId) {
        return subscriptionRepositoryPort.findById(subsId).flatMap(subs -> {
            subs.changeStatus();
            return subscriptionRepositoryPort.save(subs);
        }).then();
    }

}
