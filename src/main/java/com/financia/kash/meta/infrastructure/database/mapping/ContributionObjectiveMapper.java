package com.financia.kash.meta.infrastructure.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.meta.application.port.input.response.ContributionDTO;
import com.financia.kash.meta.domain.model.ContributeObjetive;
import com.financia.kash.meta.infrastructure.database.entity.ContributionObjectiveEntity;
import com.financia.kash.meta.infrastructure.database.repository.project.ContributionProject;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ContributionObjectiveMapper {

    ContributeObjetive mapToDomian(ContributionObjectiveEntity entity);

    ContributionObjectiveEntity mapToEntity(ContributeObjetive contributeObjetive);

    @Mapping(target = "objectiveId", ignore = true)
    @Mapping(target = "movementId", ignore = true)
    ContributeObjetive mapToDomain(ContributionProject contributionProject);

    @Mapping(source = "objective_id", target = "objective.id")
    @Mapping(source = "name_objective", target = "objective.name")
    @Mapping(source = "description_objective", target = "objective.description")
    @Mapping(source = "current_amount", target = "objective.currentAmount")
    @Mapping(source = "target_amount", target = "objective.targetAmount")
    @Mapping(source = "percentage_progress", target = "objective.percentageProgress")
    @Mapping(source = "status", target = "objective.status")
    ContributionDTO mapToDomainDTO(ContributionProject contributionProject);
}
