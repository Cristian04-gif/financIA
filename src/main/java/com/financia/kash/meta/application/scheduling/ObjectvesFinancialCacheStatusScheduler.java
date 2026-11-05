package com.financia.kash.meta.application.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.financia.kash.meta.application.port.input.UpdateStatusObjectiveUseCase;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ObjectvesFinancialCacheStatusScheduler {

    private final UpdateStatusObjectiveUseCase updateStatusObjectiveUseCase;

    @Scheduled(cron = "0 0 12 * * *")
    public void execute() {
        updateStatusObjectiveUseCase.updatEstatusExpiredGoals().subscribe();
    }

}
