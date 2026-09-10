package com.financia.kash.usuario.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.usuario.domain.model.EstadoUsuario;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.project.ProjectUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserEntityRepository extends ReactiveCrudRepository<UserEntity, UUID> {

    Flux<ProjectUser> findAllBy();

    Mono<UserEntity> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByIdAndStatus(UUID id, EstadoUsuario status);

}
