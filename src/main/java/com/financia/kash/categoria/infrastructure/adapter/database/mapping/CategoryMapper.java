package com.financia.kash.categoria.infrastructure.adapter.database.mapping;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.categoria.domain.model.Category;
import com.financia.kash.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

@Component
public class CategoryMapper {

    public CategoryEntity mapToEntity(Category category, UserEntity user) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(category.getId());
        entity.setUser(user);
        entity.setName(category.getName());
        entity.setType(category.getType());
        entity.setDefaultCategory(category.isDefaultCategory());
        entity.setCreationDate(category.getCreationDate());
        entity.setUpdateDate(category.getUpdateDate());
        entity.setActive(category.isActive());
        return entity;
    }

    public Category mapToDomain(CategoryEntity entity) {
        UUID userId = entity.getUser() == null ? null : entity.getUser().getId();
        return Category.builder()
                .id(entity.getId())
                .userId(userId)
                .name(entity.getName())
                .type(entity.getType())
                .defaultCategory(entity.isDefaultCategory())
                .creationDate(entity.getCreationDate())
                .updateDate(entity.getUpdateDate())
                .active(entity.isActive())
                .build();
    }
}
