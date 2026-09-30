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
    @Mapping(source = "account_name", target = "account.name")
    @Mapping(source = "category_id", target = "category.id")
    @Mapping(source = "category_name", target = "category.name")
    @Mapping(source = "category_type", target = "category.type")
    @Mapping(source = "date_issue", target = "dateIssue")
    @Mapping(source = "monto", target = "amount")
    @Mapping(source = "descripcion", target = "description")
    MovementDTO mapToDomainDTO(MovementProject movementProject);

}
