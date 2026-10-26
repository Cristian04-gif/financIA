package com.financia.kash.dashboard.user.application.service;

import org.springframework.stereotype.Service;

import com.financia.kash.dashboard.user.application.port.input.FinancialEvolutionUseCase;
import com.financia.kash.dashboard.user.application.port.input.command.FinancialEvolutionCommand;
import com.financia.kash.dashboard.user.application.port.output.RetrieveAllTransactionsPort;
import com.financia.kash.dashboard.user.domain.model.FinancialEvolution;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FinancialEvolutionService implements FinancialEvolutionUseCase {

    private final RetrieveAllTransactionsPort retrieveAllTransactionsPort;

    @Override
    public Mono<FinancialEvolution> financialEvolution(FinancialEvolutionCommand command) {
        return retrieveAllTransactionsPort.retrievePeriodMovement(command.userId(), 12).collectList()
                .map(periods -> new FinancialEvolution("PEN", periods));
    }

}
