package com.financia.kash.movimiento.suscripcion.application.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.financia.kash.movimiento.suscripcion.application.port.input.SubscriptionDiscountuseCase;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionChargeScheduler {

    private final SubscriptionDiscountuseCase subscriptionDiscountuseCase;

    @Scheduled(cron = "0 0 0 * * *")
    public void processSubscriptionPayment() {
        subscriptionDiscountuseCase.subscriptionDiscount().subscribe();
    }
}
