package com.financia.kash.presupuesto.application.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.financia.kash.presupuesto.application.port.input.ChangeBudgetStatusUseCase;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpdateBudgetStatusScheduler {

    private final ChangeBudgetStatusUseCase changeBudgetStatusUseCase;

    @Scheduled(cron = "0 0 12 * * *")
    public void processSubscriptionPayment() {
        changeBudgetStatusUseCase.updateExpiringToday().subscribe();

    }
}
