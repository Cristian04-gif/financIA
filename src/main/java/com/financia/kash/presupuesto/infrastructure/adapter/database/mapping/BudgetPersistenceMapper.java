package com.financia.kash.presupuesto.infrastructure.adapter.database.mapping;

import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import com.financia.kash.presupuesto.domain.model.Budget;
import com.financia.kash.presupuesto.domain.model.BudgetCategory;
import com.financia.kash.presupuesto.infrastructure.adapter.database.entity.BudgetEntity;
import com.financia.kash.presupuesto.infrastructure.adapter.database.entity.BudgetCategoryEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BudgetPersistenceMapper {
    BudgetEntity toEntity(Budget budget);
    BudgetCategory toDomain(BudgetCategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "budgetId", source = "budgetId")
    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "amountLimit", source = "category.amountLimit")
    BudgetCategoryEntity toEntity(BudgetCategory category, UUID budgetId);

    default Budget toDomain(BudgetEntity entity, List<BudgetCategory> categories) {
        return new Budget(entity.getId(), entity.getUserId(), entity.getName(),
                entity.getPeriodStart(), entity.getPeriodEnd(), entity.getAmountLimitTotal(),
                entity.isActive(), entity.getCreationDate(), entity.getUpdateDate(), categories);
    }
}

