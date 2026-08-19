package com.financia.kash.meta.infrastructure.adapter.database.mapping;

import org.springframework.stereotype.Component;

import com.financia.kash.meta.domain.model.FinancialGoal;
import com.financia.kash.meta.infrastructure.adapter.database.entity.FinancialGoalEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

@Component
public class FinancialGoalMapper {

    public FinancialGoalEntity mapToEntity(FinancialGoal financialGoal, UserEntity user) {
        FinancialGoalEntity entity = new FinancialGoalEntity();
        entity.setId(financialGoal.getId());
        entity.setUser(user);
        entity.setName(financialGoal.getName());
        entity.setTargetAmount(financialGoal.getTargetAmount());
        entity.setCurrentAmount(financialGoal.getCurrentAmount());
        entity.setTargetDate(financialGoal.getTargetDate());
        entity.setStatus(financialGoal.getStatus());
        entity.setCreationDate(financialGoal.getCreationDate());
        entity.setUpdateDate(financialGoal.getUpdateDate());
        return entity;
    }

    public FinancialGoal mapToDomain(FinancialGoalEntity entity) {
        return FinancialGoal.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .name(entity.getName())
                .targetAmount(entity.getTargetAmount())
                .currentAmount(entity.getCurrentAmount())
                .targetDate(entity.getTargetDate())
                .status(entity.getStatus())
                .creationDate(entity.getCreationDate())
                .updateDate(entity.getUpdateDate())
                .build();
    }
}
