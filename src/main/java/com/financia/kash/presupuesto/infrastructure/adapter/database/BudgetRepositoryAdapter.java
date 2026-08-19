package com.financia.kash.presupuesto.infrastructure.adapter.database;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.presupuesto.application.port.output.BudgetRepositoryPort;
import com.financia.kash.presupuesto.domain.model.Budget;
import com.financia.kash.presupuesto.infrastructure.adapter.database.mapping.BudgetMapper;
import com.financia.kash.presupuesto.infrastructure.adapter.database.repository.BudgetEntityRepository;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BudgetRepositoryAdapter implements BudgetRepositoryPort {

    private final BudgetEntityRepository budgetEntityRepository;
    private final BudgetMapper budgetMapper;
    private final EntityManager entityManager;

    @Override
    public Budget save(Budget budget) {
        UserEntity user = entityManager.getReference(UserEntity.class, budget.getUserId());
        CategoryEntity category = entityManager.getReference(CategoryEntity.class, budget.getCategoryId());
        return budgetMapper.mapToDomain(budgetEntityRepository.save(budgetMapper.mapToEntity(budget, user, category)));
    }

    @Override
    public Budget findById(UUID id) {
        return budgetEntityRepository.findById(id)
                .map(budgetMapper::mapToDomain)
                .orElseThrow(() -> new IllegalArgumentException("Presupuesto no encontrado: " + id));
    }

    @Override
    public List<Budget> findByUserId(UUID userId) {
        return budgetEntityRepository.findAllByUser_Id(userId).stream()
                .map(budgetMapper::mapToDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        budgetEntityRepository.deleteById(id);
    }
}
