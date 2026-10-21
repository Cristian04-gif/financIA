package com.financia.kash.movimiento.suscripcion.application.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.movimiento.categoria.application.port.input.GetCategoriesUseCase;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.movimiento.application.port.output.MovementRepositoryPort;
import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovement;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.movimiento.suscripcion.application.port.input.ChangeStatusSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.CreateSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.GetSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.IncreaseFundsUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.SubscriptionDiscountuseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.command.CreateSubscriptionCommand;
import com.financia.kash.movimiento.suscripcion.application.port.output.FindAccountForSubscription;
import com.financia.kash.movimiento.suscripcion.application.port.output.SaveAccountoForSubscription;
import com.financia.kash.movimiento.suscripcion.application.port.output.SubscriptionRepositoryPort;
import com.financia.kash.movimiento.suscripcion.domain.model.Subscription;
import com.financia.kash.shared.application.service.CacheService;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SubscriptionService
        implements GetSubscriptionUseCase, CreateSubscriptionUseCase, ChangeStatusSubscriptionUseCase,
        SubscriptionDiscountuseCase, IncreaseFundsUseCase {

    private final SubscriptionRepositoryPort subscriptionRepositoryPort;
    private final GetCategoriesUseCase getCategoriesUseCase;
    private final FindAccountForSubscription findAccountForSubscription;
    private final SaveAccountoForSubscription saveAccountoForSubscription;
    private final MovementRepositoryPort movementRepositoryPort;
    private final CacheService<Subscription> cacheService;

    @Override
    public Flux<Subscription> getSubscriptionForUser(UUID userId) {
        String cacheKey = CacheKeys.allUserSubscription(userId);
        return cacheService.getCacheList(cacheKey, Subscription.class)
                .switchIfEmpty(subscriptionRepositoryPort.findAllByUser(userId).collectList()
                        .flatMapMany(subs -> cacheService.saveListCache(cacheKey, subs, 60)));
        // return subscriptionRepositoryPort.findAllByUser(userId);
    }

    @Override
    public Mono<Subscription> getById(UUID subsId) {
        String cacheKey = CacheKeys.subscription(subsId);
        return cacheService.retrieveMonoCache(cacheKey, Subscription.class)
                .switchIfEmpty(subscriptionRepositoryPort.findById(subsId))
                .flatMap(sub -> cacheService.saveMonoCache(cacheKey, sub, 60));
        // return subscriptionRepositoryPort.findById(subsId);
    }

    @Override
    public Mono<Subscription> create(CreateSubscriptionCommand command) {
        Mono<Account> accountMono = findAccountForSubscription.findMyAccountById(command.payingAccountId());
        Mono<Category> categoryMono = getCategoriesUseCase.getById(command.categoryId());

        return Mono.zip(accountMono, categoryMono).flatMap(tuple -> {
            Account account = tuple.getT1();
            Category category = tuple.getT2();

            return subscriptionRepositoryPort
                    .save(Subscription.create(command.userId(), account.getId(), category.getId(), command.name(),
                            command.amount(), command.frequency(), command.type(),
                            command.payDay()))
                    .flatMap(sub -> CacheKeys.deleteAllCacheUserSubscription(cacheService.redisTemplate(),
                            sub.getUserId()).thenReturn(sub));
        });
    }

    @Override
    public Mono<Void> changeStatusSubscription(UUID subsId) {
        return subscriptionRepositoryPort.findById(subsId).flatMap(subs -> {
            subs.changeStatus();
            return subscriptionRepositoryPort.save(subs)
                    .then(CacheKeys.clearCacheSubscription(cacheService.redisTemplate(), subs.getUserId(), subsId));
        }).then();
    }

    @Override
    @Transactional
    public Mono<Void> subscriptionDiscount() {
        return subscriptionRepositoryPort
                .findSubscriptionsExpiringToday(LocalDate.now())
                .filter(subs -> subs.getType().equals(TypeMovement.EGRESO))
                .concatMap(sub -> findAccountForSubscription.findMyAccountById(sub.getPayingAccountId())
                        .flatMap(account -> {
                            account.validateAccountIsActive();
                            account.validateSufficientFunds(sub.getAmount());
                            account.transfer(sub.getAmount());

                            Movement movement = new Movement(
                                    sub.getUserId(),
                                    account.getId(),
                                    sub.getCategoryId(),
                                    sub.getId(),
                                    TypeMovement.EGRESO,
                                    sub.getAmount(),
                                    LocalDate.now(),
                                    "Se cobro la suscripcion a " + sub.getName() + " con la cuenta "
                                            + account.getName());

                            return movementRepositoryPort
                                    .save(movement)
                                    .then(saveAccountoForSubscription.save(account)).then(CacheKeys
                                            .clearAllCacheSubscription(cacheService.redisTemplate(), sub.getUserId(),
                                                    sub.getId(), sub.getPayingAccountId()));
                        }))
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> increaseFunds() {
        return subscriptionRepositoryPort.findSubscriptionsExpiringToday(LocalDate.now())
                .filter(movements -> movements.getType().equals(TypeMovement.INGRESO))
                .concatMap(ingreso -> findAccountForSubscription.findMyAccountById(ingreso.getPayingAccountId())
                        .flatMap(account -> {
                            account.receive(ingreso.getAmount());

                            Movement movement = new Movement(
                                    ingreso.getUserId(),
                                    account.getId(),
                                    ingreso.getCategoryId(),
                                    ingreso.getId(),
                                    TypeMovement.INGRESO,
                                    ingreso.getAmount(),
                                    LocalDate.now(),
                                    "Se sumo el ingreso del sueldo " + ingreso.getName() + " a la cuenta "
                                            + account.getName());
                            return movementRepositoryPort.save(movement)
                                    .then(saveAccountoForSubscription.save(account)).then(CacheKeys
                                            .clearAllCacheSubscription(cacheService.redisTemplate(),
                                                    ingreso.getUserId(), ingreso.getId(),
                                                    ingreso.getPayingAccountId()));
                        }))
                .then();
    }

}
