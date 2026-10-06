package com.financia.kash.presupuesto.infrastructure.adapter.database.repository;

import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.financia.kash.presupuesto.infrastructure.adapter.database.entity.BudgetEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BudgetEntityRepository extends ReactiveCrudRepository<BudgetEntity, UUID> {
    Flux<BudgetEntity> findAllByUserIdOrderByCreationDateDescIdAsc(UUID userId);
    Mono<BudgetEntity> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT * FROM presupuestos WHERE id = :id AND usuario_id = :userId FOR UPDATE")
    Mono<BudgetEntity> findOwnedForUpdate(UUID id, UUID userId);
}
