package com.financia.kash.meta.infrastructure.database.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.meta.infrastructure.database.entity.ContributionObjectiveEntity;
import com.financia.kash.meta.infrastructure.database.repository.project.ContributionProject;

import reactor.core.publisher.Mono;

public interface ContributionObjectiveRepository extends ReactiveCrudRepository<ContributionObjectiveEntity, UUID> {

        Mono<Long> countByObjectiveId(UUID objectiveId);

        @Query("""
                        SELECT
                            am.id,
                            m.id AS objective_id,
                            m.nombre AS name_objective,
                            m.descripcion AS description_objective,
                            m.monto_actual AS current_amount,
                            m.monto_objetivo AS target_amount,
                            m.progreso AS percentage_progress,
                            m.estado AS status,
                            am.monto AS amount,
                            am.fecha AS date,
                            am.descripcion AS description
                        FROM metas_financieras m
                        INNER JOIN aportes_meta am
                            ON m.id = am.meta_id
                        WHERE am.id = :contributionId
                                                """)
        Mono<ContributionProject> findByIdProyect(UUID contributionId);

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
