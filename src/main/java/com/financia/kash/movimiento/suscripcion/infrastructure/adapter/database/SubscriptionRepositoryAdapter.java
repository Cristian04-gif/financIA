package com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.movimiento.suscripcion.application.port.output.SubscriptionRepositoryPort;
import com.financia.kash.movimiento.suscripcion.domain.exception.SubscriptionNotfoundException;
import com.financia.kash.movimiento.suscripcion.domain.model.Subscription;
import com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.mapping.SubscriptionMapper;
import com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.repository.SubscriptionEntityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryAdapter implements SubscriptionRepositoryPort {

    private final SubscriptionEntityRepository subscriptionEntityRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    public Flux<Subscription> findAllByUser(UUID userId) {
        return subscriptionEntityRepository.findAllByUserId(userId).map(subscriptionMapper::mapToDomain);
    }

    @Override
    public Mono<Subscription> findById(UUID subsId) {
        return subscriptionEntityRepository.findById(subsId)
                .switchIfEmpty(Mono.error(new SubscriptionNotfoundException(subsId)))
                .map(subscriptionMapper::mapToDomain);
    }

    @Override
    public Mono<Subscription> save(Subscription subscription) {
        return Mono.just(subscriptionMapper.mapToEntity(subscription)).flatMap(subscriptionEntityRepository::save)
                .map(subscriptionMapper::mapToDomain);
    }

    @Override
    public Flux<Subscription> findSubscriptionsExpiringToday(LocalDate date) {
        return subscriptionEntityRepository.findByDateNextPayment(date).map(subscriptionMapper::mapToDomain);
    }

}
