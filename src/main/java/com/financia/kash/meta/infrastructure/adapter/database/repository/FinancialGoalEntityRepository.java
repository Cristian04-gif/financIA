package com.financia.kash.meta.infrastructure.adapter.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financia.kash.meta.infrastructure.adapter.database.entity.FinancialGoalEntity;

public interface FinancialGoalEntityRepository extends JpaRepository<FinancialGoalEntity, UUID> {
    List<FinancialGoalEntity> findAllByUser_Id(UUID userId);
}
