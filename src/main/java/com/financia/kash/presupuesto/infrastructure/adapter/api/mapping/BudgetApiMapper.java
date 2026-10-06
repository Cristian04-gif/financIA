package com.financia.kash.presupuesto.infrastructure.adapter.api.mapping;

import org.mapstruct.*;
import com.financia.kash.presupuesto.application.model.*;
import com.financia.kash.presupuesto.domain.model.*;
import com.financia.kash.presupuesto.infrastructure.adapter.api.dto.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BudgetApiMapper {
    BudgetCommand toCommand(BudgetRequest request);
    @Mapping(target = "id", ignore = true)
    BudgetCategory toDomain(BudgetCategoryRequest request);
    BudgetResponse toResponse(Budget budget);
    BudgetCategoryResponse toResponse(BudgetCategory category);
    BudgetConsumptionResponse toResponse(BudgetConsumption consumption);
    BudgetCategoryConsumptionResponse toResponse(BudgetCategoryConsumption category);
}

