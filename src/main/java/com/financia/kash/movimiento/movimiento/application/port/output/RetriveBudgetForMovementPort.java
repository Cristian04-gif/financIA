package com.financia.kash.movimiento.movimiento.application.port.output;

import java.util.UUID;

import com.financia.kash.presupuesto.domain.model.Budget;

import reactor.core.publisher.Mono;

public interface RetriveBudgetForMovementPort {
    Mono<Budget> retriveBudgetBtUserIdAndCategoryId(UUID userId, UUID categoryId);
}
