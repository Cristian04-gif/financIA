package com.financia.kash.meta.application.port.input;

import java.util.UUID;

import com.financia.kash.meta.domain.model.FinancialObjectives;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface RetriveObjectivesUseCase {
    Mono<PaginationResponse<FinancialObjectives>> getAllObjecives(UUID userId, PaginationRequest request);

    Mono<FinancialObjectives> getById(UUID id);
}
