package com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.project.AccountProject;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountEntityRepository extends ReactiveCrudRepository<AccountEntity, UUID> {
    Flux<AccountProject> findByUserId(UUID userId);

    @Query("""
                SELECT a FROM AccountEntity a
                INNER JOIN a.usuario u
                WHERE a.id = :id AND u.email = :email
            """)
    Mono<AccountEntity> findByIdAndUserEmail(UUID id, String email);

}
