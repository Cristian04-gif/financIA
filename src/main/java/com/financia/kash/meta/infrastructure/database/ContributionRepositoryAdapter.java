package com.financia.kash.meta.infrastructure.database;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.financia.kash.meta.application.port.input.response.ContributionDTO;
import com.financia.kash.meta.application.port.output.ContributeObjectiveRepositoryPort;
import com.financia.kash.meta.domain.exception.ContributionNotFoundException;
import com.financia.kash.meta.domain.model.ContributeObjetive;
import com.financia.kash.meta.infrastructure.database.mapping.ContributionObjectiveMapper;
import com.financia.kash.meta.infrastructure.database.repository.ContributionEntityQueryRepository;
import com.financia.kash.meta.infrastructure.database.repository.ContributionObjectiveRepository;
import com.financia.kash.meta.infrastructure.database.repository.project.ContributionProject;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ContributionRepositoryAdapter implements ContributeObjectiveRepositoryPort {

    private final ContributionObjectiveRepository contributionObjectiveRepository;
    private final ContributionObjectiveMapper contributionObjectiveMapper;
    private final ContributionEntityQueryRepository contributionEntityQueryRepository;

    @Override
    public Mono<PaginationResponse<ContributionDTO>> findAllByObjectiveId(UUID objectiveId,
            PaginationRequest request) {
        Flux<ContributionProject> pageContribution = contributionEntityQueryRepository
                .findAllContributionByObjective(objectiveId, request);

        Mono<Long> count = contributionObjectiveRepository.countByObjectiveId(objectiveId);

        return Mono.zip(pageContribution.map(contributionObjectiveMapper::mapToDomainDTO).collectList(), count)
                .map(tuple -> {
                    List<ContributionDTO> contributeObjetives = tuple.getT1();
                    long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / request.getPageSize());
                    boolean isLast = request.getPageNum() >= Math.max(0, totalPages - 1);

                    return new PaginationResponse<>(contributeObjetives, request.getPageNum(),
                            request.getPageSize(), totalPages, totalElements, isLast);
                });
    }

    @Override
    public Mono<ContributeObjetive> findById(UUID id) {
        return contributionObjectiveRepository.findById(id)
                .switchIfEmpty(Mono.error(new ContributionNotFoundException(id)))
                .map(contributionObjectiveMapper::mapToDomian);
    }

    @Override
    public Mono<ContributeObjetive> save(ContributeObjetive contributeObjetive) {
        return Mono.fromCallable(() -> {
            return contributionObjectiveMapper.mapToEntity(contributeObjetive);
        }).flatMap(contributionObjectiveRepository::save).map(contributionObjectiveMapper::mapToDomian);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return contributionObjectiveRepository.deleteById(id);
    }

    @Override
    public Mono<ContributionDTO> findByIdDTO(UUID id) {
        return contributionObjectiveRepository.findByIdProyect(id)
                .switchIfEmpty(Mono.error(new ContributionNotFoundException(id)))
                .map(contributionObjectiveMapper::mapToDomainDTO);
    }

}
