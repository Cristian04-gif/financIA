package com.financia.kash.dashboard.user.application.port.output;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.dashboard.user.domain.model.MovementsMonth;
import com.financia.kash.dashboard.user.domain.model.ExpensesCategory.CategoriesExcenses;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RetrieveAllTransactionsPort {

        Flux<MovementsMonth> retrievePeriodMovement(UUID userId, Integer limit);

        Mono<MovementsMonth> retriveMovementsMotnh(UUID userId);

        Flux<Movement> retrieveAllTransactionByCategory(UUID userId, UUID categoryId, LocalDateTime from,
                        LocalDateTime until);

        Flux<CategoriesExcenses> findExpensesByCategory(UUID userId, LocalDate from, LocalDate until);

}
