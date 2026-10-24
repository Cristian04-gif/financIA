package com.financia.kash.dashboard.user.application.port.output;

import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.movimiento.movimiento.domain.model.Movement;

import reactor.core.publisher.Flux;

public interface RetrieveAllTransactionsPort {
        Flux<Movement> retrieveAllTransactions(UUID userId, LocalDateTime from,
                        LocalDateTime until);

        Flux<Movement> retrieveAllTransactionByCategory(UUID userId, UUID categoryId, LocalDateTime from,
                        LocalDateTime until);
}
