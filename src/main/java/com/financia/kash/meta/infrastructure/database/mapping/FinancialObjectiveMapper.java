package com.financia.kash.meta.infrastructure.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.meta.domain.model.FinancialObjectives;
import com.financia.kash.meta.infrastructure.database.entity.FinancialObjectiveEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FinancialObjectiveMapper {

    FinancialObjectives mapToDomain(FinancialObjectiveEntity entity);

    FinancialObjectiveEntity mapToEntity(FinancialObjectives objectives);
}
