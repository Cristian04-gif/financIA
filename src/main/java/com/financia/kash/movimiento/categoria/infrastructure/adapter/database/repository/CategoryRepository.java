package com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.project.ProjectCategory;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    List<ProjectCategory> findByUserIsNullAndParentCategoryIdIsNull();

    List<ProjectCategory> findAllByUserId(UUID id);

}
