package com.financia.kash.presupuesto.infrastructure.adapter.database.mapping;

import org.springframework.stereotype.Component;

import com.financia.kash.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.presupuesto.domain.model.Budget;
import com.financia.kash.presupuesto.infrastructure.adapter.database.entity.BudgetEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

@Component
public class BudgetMapper {

    public BudgetEntity mapToEntity(Budget budget, UserEntity user, CategoryEntity category) {
        BudgetEntity entity = new BudgetEntity();
        entity.setId(budget.getId());
        entity.setUser(user);
        entity.setCategory(category);
        entity.setName(budget.getName());
        entity.setLimitAmount(budget.getLimitAmount());
        entity.setSpentAmount(budget.getSpentAmount());
        entity.setStartDate(budget.getStartDate());
        entity.setEndDate(budget.getEndDate());
        entity.setCreationDate(budget.getCreationDate());
        entity.setUpdateDate(budget.getUpdateDate());
        entity.setActive(budget.isActive());
        return entity;
    }

    public Budget mapToDomain(BudgetEntity entity) {
        return Budget.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .categoryId(entity.getCategory().getId())
                .name(entity.getName())
                .limitAmount(entity.getLimitAmount())
                .spentAmount(entity.getSpentAmount())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .creationDate(entity.getCreationDate())
                .updateDate(entity.getUpdateDate())
                .active(entity.isActive())
                .build();
    }
}
