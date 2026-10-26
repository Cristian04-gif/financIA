package com.financia.kash.movimiento.suscripcion.application.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.financia.kash.movimiento.suscripcion.application.port.input.IncreaseFundsUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.SubscriptionDiscountuseCase;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionChargeScheduler {

    private final SubscriptionDiscountuseCase subscriptionDiscountuseCase;
    private final IncreaseFundsUseCase increaseFundsUseCase;

    @Scheduled(cron = "0 0 12 * * *")
    public void processSubscriptionPayment() {
        subscriptionDiscountuseCase.subscriptionDiscount().subscribe();

        increaseFundsUseCase.increaseFunds().subscribe();

    }
}
