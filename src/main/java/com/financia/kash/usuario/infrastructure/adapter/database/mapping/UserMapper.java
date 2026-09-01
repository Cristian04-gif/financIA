package com.financia.kash.usuario.infrastructure.adapter.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.usuario.domain.model.User;
import com.financia.kash.usuario.infrastructure.adapter.api.dto.UserResponseDTO;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.project.ProjectUser;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "transfers", ignore = true)
    UserEntity mapToEntity(User user);

    User mapToDomain(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "secret2fa", ignore = true)
    @Mapping(target = "enable2fa", ignore = true)
    User mapToProject(ProjectUser projectUser);

    UserResponseDTO mapToDTO(User user);
}
