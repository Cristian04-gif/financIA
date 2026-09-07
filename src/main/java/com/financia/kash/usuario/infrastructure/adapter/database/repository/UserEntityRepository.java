package com.financia.kash.usuario.infrastructure.adapter.database.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financia.kash.usuario.domain.model.EstadoUsuario;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.project.ProjectUser;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {

    List<ProjectUser> findAllBy();

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByIdAndStatus(UUID id, EstadoUsuario status);

}
