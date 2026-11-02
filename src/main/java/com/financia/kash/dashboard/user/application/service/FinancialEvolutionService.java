package com.financia.kash.dashboard.user.application.service;

import org.springframework.stereotype.Service;

import com.financia.kash.dashboard.user.application.port.input.FinancialEvolutionUseCase;
import com.financia.kash.dashboard.user.application.port.input.command.FinancialEvolutionCommand;
import com.financia.kash.dashboard.user.application.port.output.RetrieveAllTransactionsPort;
import com.financia.kash.dashboard.user.domain.model.FinancialEvolution;

import com.financia.kash.shared.application.service.CacheService;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FinancialEvolutionService implements FinancialEvolutionUseCase {

    private final RetrieveAllTransactionsPort retrieveAllTransactionsPort;
    private final CacheService<FinancialEvolution> cacheService;

    @Override
    public Mono<FinancialEvolution> financialEvolution(FinancialEvolutionCommand command) {
        String cacheKey = CacheKeys.financialEvolution(command.userId());

        return cacheService.retrieveMonoCache(cacheKey, FinancialEvolution.class)
                .switchIfEmpty(Mono.defer(() -> computeFinancialEvolution(command))
                        .flatMap(evolution -> cacheService.saveMonoCache(cacheKey, evolution, 15)));
    }

    private Mono<FinancialEvolution> computeFinancialEvolution(FinancialEvolutionCommand command) {
        return retrieveAllTransactionsPort.retrievePeriodMovement(command.userId(), 12).collectList()
                .map(periods -> new FinancialEvolution("PEN", periods));
    }

}
