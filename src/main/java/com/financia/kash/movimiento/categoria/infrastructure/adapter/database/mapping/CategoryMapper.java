package com.financia.kash.movimiento.categoria.infrastructure.adapter.database.mapping;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.api.dto.CategoryResponse;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.project.ProjectCategory;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    @Mapping(source = "userId", target = "user")
    @Mapping(target = "ownerEmail", ignore = true)
    CategoryEntity mapToEntity(Category category);

    @Mapping(source = "user.id", target = "userId")
    Category mapToDomain(CategoryEntity categoryEntity);

    @Mapping(target = "parentCategoryId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Category mapToDomain(ProjectCategory projectCategory);

    ProjectCategory mapToProject(Category category);

    CategoryResponse mapToResponse(Category category);

    default UserEntity mapUser(UUID userId) {
        if (userId == null) {
            return null;
        }

        UserEntity user = new UserEntity();
        user.setId(userId);
        return user;
    }

}
