package com.financia.kash.meta.infrastructure.database.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.meta.infrastructure.database.entity.ContributionObjectiveEntity;
import com.financia.kash.meta.infrastructure.database.repository.project.ContributionProject;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContributionObjectiveRepository extends ReactiveCrudRepository<ContributionObjectiveEntity, UUID> {

    Flux<ContributionProject> findAllByObjectiveId(UUID objectiveId, Pageable pageable);

    Mono<Long> countByObjectiveId(UUID objectiveId);

    @Query("""
            SELECT EXISTS(
                SELECT 1
                FROM aportes_meta a
                LEFT JOIN metas_financieras m ON m.id = a.objetivo_id
                LEFT JOIN usuarios u ON u.id = a.usuario_id
                WHERE a.id = :id
                AND u.email = :email
            )
                   """)
    Mono<Boolean> existsByIdAndOwnerEmail(UUID id, String email);
}
