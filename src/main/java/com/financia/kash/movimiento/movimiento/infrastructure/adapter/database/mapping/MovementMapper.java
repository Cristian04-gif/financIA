package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.movimiento.movimiento.domain.model.dto.MovementDTO;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.entity.MovementEntity;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.project.MovementProject;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MovementMapper {

    Movement mapToDomain(MovementEntity entity);

    MovementEntity mapToEntity(Movement motion);

    @Mapping(source = "movement_id", target = "movementId")
    @Mapping(source = "account_id", target = "account.id")
    @Mapping(source = "category_id", target = "category.id")
    @Mapping(source = "date_issue", target = "dateIssue")
    MovementDTO mapToDomainDTO(MovementProject movementProject);

}
