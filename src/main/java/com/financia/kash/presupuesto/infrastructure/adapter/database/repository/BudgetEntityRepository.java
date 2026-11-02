package com.financia.kash.presupuesto.infrastructure.adapter.database.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.financia.kash.presupuesto.infrastructure.adapter.database.entity.BudgetEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BudgetEntityRepository extends ReactiveCrudRepository<BudgetEntity, UUID> {
    Flux<BudgetEntity> findAllByUserIdOrderByCreationDateDescIdAsc(UUID userId);

    Mono<BudgetEntity> findByIdAndUserId(UUID id, UUID userId);

    Flux<BudgetEntity> findAllByUserIdAndCreationDateBetween(UUID userId, LocalDateTime from, LocalDateTime until);

    @Query("SELECT * FROM presupuestos WHERE id = :id AND usuario_id = :userId FOR UPDATE")
    Mono<BudgetEntity> findOwnedForUpdate(UUID id, UUID userId);

    Flux<BudgetEntity> findByPeriodEndAndActiveTrue(LocalDate date);

    @Query("""
            SELECT p.*
                FROM presupuestos p
                INNER JOIN presupuesto_categorias pc ON p.id = pc.presupuesto_id
                WHERE p.usuario_id = :userId
                    AND pc.categoria_id = :categoryId
                    AND p.periodo_fin >= CURRENT_DATE AND p.activo = true
                        """)
    Mono<BudgetEntity> findBudgetByUserAndCategory(UUID userId, UUID categoryId);
}
