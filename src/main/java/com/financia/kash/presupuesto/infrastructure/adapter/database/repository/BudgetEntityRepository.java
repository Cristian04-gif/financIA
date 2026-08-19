package com.financia.kash.presupuesto.infrastructure.adapter.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financia.kash.presupuesto.infrastructure.adapter.database.entity.BudgetEntity;

public interface BudgetEntityRepository extends JpaRepository<BudgetEntity, UUID> {
    List<BudgetEntity> findAllByUser_Id(UUID userId);
}
