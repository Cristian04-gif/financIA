package com.financia.kash.movimiento.categoria.infrastructure.adapter.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.api.dto.CategoryResponse;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.project.ProjectCategory;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    CategoryEntity mapToEntity(Category category);

    Category mapToDomain(CategoryEntity categoryEntity);

    @Mapping(target = "parentCategoryId", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Category mapToDomain(ProjectCategory projectCategory);

    ProjectCategory mapToProject(Category category);

    CategoryResponse mapToResponse(Category category);

}
