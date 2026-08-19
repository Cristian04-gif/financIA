package com.financia.kash.categoria.infrastructure.adapter.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financia.kash.categoria.infrastructure.adapter.database.entity.CategoryEntity;

public interface CategoryEntityRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findAllByUser_IdOrDefaultCategoryTrue(UUID userId);
}
