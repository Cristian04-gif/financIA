package com.financia.kash.meta.application.port.input;

import java.util.UUID;

import com.financia.kash.meta.application.port.input.response.ContributionDTO;
import com.financia.kash.meta.domain.model.ContributeObjetive;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface RetriveContributionUseCase {

    Mono<PaginationResponse<ContributionDTO>> getAllByObjective(UUID objectiveId,
            PaginationRequest paginationRequest);

    Mono<ContributeObjetive> getById(UUID id);
}
