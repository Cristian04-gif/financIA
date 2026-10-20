package com.financia.kash.meta.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.meta.application.port.input.RetriveContributionUseCase;
import com.financia.kash.meta.application.port.input.response.ContributionDTO;
import com.financia.kash.meta.application.port.output.ContributeObjectiveRepositoryPort;
import com.financia.kash.shared.application.service.CacheService;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ContributionService implements RetriveContributionUseCase {

    private final ContributeObjectiveRepositoryPort contributeObjectiveRepositoryPort;
    private final CacheService<ContributionDTO> cacheService;

    @Override
    public Mono<PaginationResponse<ContributionDTO>> getAllByObjective(UUID objectiveId,
            PaginationRequest paginationRequest) {
        String cacheKey = CacheKeys.allContributionObjective(objectiveId, paginationRequest);
        return cacheService.retriveCachePagination(cacheKey, ContributionDTO.class)
                .switchIfEmpty(contributeObjectiveRepositoryPort.findAllByObjectiveId(objectiveId, paginationRequest))
                .flatMap(contributions -> cacheService.saveCachePagination(cacheKey, contributions));
        // return contributeObjectiveRepositoryPort.findAllByObjectiveId(objectiveId,
        // paginationRequest);
    }

    @Override
    public Mono<ContributionDTO> getById(UUID id) {
        String cacheKey = CacheKeys.contributionObjective(id);
        return cacheService.retrieveMonoCache(cacheKey, ContributionDTO.class)
                .switchIfEmpty(contributeObjectiveRepositoryPort.findByIdDTO(id))
                .flatMap(contribution -> cacheService.saveMonoCache(cacheKey, contribution));
        // return contributeObjectiveRepositoryPort.findByIdDTO(id);
    }

}
