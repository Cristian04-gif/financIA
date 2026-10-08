package com.financia.kash.movimiento.suscripcion.application.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.movimiento.categoria.application.port.input.GetCategoriesUseCase;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.movimiento.application.port.output.MovementRepositoryPort;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.movimiento.movimiento.domain.model.TypeMovement;
import com.financia.kash.movimiento.suscripcion.application.port.input.ChangeStatusSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.CreateSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.GetSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.SubscriptionDiscountuseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.command.CreateSubscriptionCommand;
import com.financia.kash.movimiento.suscripcion.application.port.output.FindAccountForSubscription;
import com.financia.kash.movimiento.suscripcion.application.port.output.SaveAccountoForSubscription;
import com.financia.kash.movimiento.suscripcion.application.port.output.SubscriptionRepositoryPort;
import com.financia.kash.movimiento.suscripcion.domain.model.Subscription;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SubscriptionService
        implements GetSubscriptionUseCase, CreateSubscriptionUseCase, ChangeStatusSubscriptionUseCase,
        SubscriptionDiscountuseCase {

    private final SubscriptionRepositoryPort subscriptionRepositoryPort;
    private final GetCategoriesUseCase getCategoriesUseCase;
    private final FindAccountForSubscription findAccountForSubscription;
    private final SaveAccountoForSubscription saveAccountoForSubscription;
    private final MovementRepositoryPort movementRepositoryPort;

    @Override
    public Flux<Subscription> getSubscriptionForUser(UUID userId) {
        return subscriptionRepositoryPort.findAllByUser(userId);
    }

    @Override
    public Mono<Subscription> getById(UUID subsId) {
        return subscriptionRepositoryPort.findById(subsId);
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
                            command.amount(), command.frequency(),
                            command.payDay()));
        });
    }

    @Override
    public Mono<Void> changeStatusSubscription(UUID subsId) {
        return subscriptionRepositoryPort.findById(subsId).flatMap(subs -> {
            subs.changeStatus();
            return subscriptionRepositoryPort.save(subs);
        }).then();
    }

    @Override
    @Transactional
    public Mono<Void> subscriptionDiscount() {
        return subscriptionRepositoryPort
                .findSubscriptionsExpiringToday(LocalDate.now()).filter(subs -> subs.getActive().equals(Boolean.TRUE))
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
                                    "Se cobro la suscripcion a " + sub.getName());

                            return movementRepositoryPort
                                    .save(movement)
                                    .then(saveAccountoForSubscription.save(account));
                        }))
                .then();
    }

}
