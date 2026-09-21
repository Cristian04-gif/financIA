package com.financia.kash.usuario.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.project.ProjectUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserEntityRepository extends ReactiveCrudRepository<UserEntity, UUID> {

    Flux<ProjectUser> findAllBy();

    Mono<UserEntity> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    @Query("""
                SELECT EXISTS(
                SELECT 1
                FROM usuarios as u
                where u.email = :email AND u.estado ='ACTIVO'
            )
            """)
    Mono<Boolean> isBlocked(String email);

}
