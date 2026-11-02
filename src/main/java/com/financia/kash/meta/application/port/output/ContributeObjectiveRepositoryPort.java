package com.financia.kash.meta.application.port.output;

import java.util.UUID;

import com.financia.kash.meta.application.port.input.response.ContributionDTO;
import com.financia.kash.meta.domain.model.ContributeObjetive;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import reactor.core.publisher.Mono;

public interface ContributeObjectiveRepositoryPort {
    Mono<PaginationResponse<ContributionDTO>> findAllByObjectiveId(UUID objectiveId, PaginationRequest request);

    Mono<ContributionDTO> findByIdDTO(UUID id);

    Mono<ContributeObjetive> findById(UUID id);

    Mono<ContributeObjetive> save(ContributeObjetive contributeObjetive);

    Mono<Void> deleteById(UUID id);
}
