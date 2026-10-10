package com.financia.kash.meta.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.meta.application.port.input.RetriveContributionUseCase;
import com.financia.kash.meta.application.port.input.response.ContributionDTO;
import com.financia.kash.meta.application.port.output.ContributeObjectiveRepositoryPort;
import com.financia.kash.meta.domain.model.ContributeObjetive;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ContributionService implements RetriveContributionUseCase {

    private final ContributeObjectiveRepositoryPort contributeObjectiveRepositoryPort;

    @Override
    public Mono<PaginationResponse<ContributionDTO>> getAllByObjective(UUID objectiveId,
            PaginationRequest paginationRequest) {
        return contributeObjectiveRepositoryPort.findAllByObjectiveId(objectiveId, paginationRequest);
    }

    @Override
    public Mono<ContributeObjetive> getById(UUID id) {
        return contributeObjectiveRepositoryPort.findById(id);
    }

}
