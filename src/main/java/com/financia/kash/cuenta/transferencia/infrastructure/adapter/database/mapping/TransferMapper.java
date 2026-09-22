package com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.cuenta.transferencia.domain.model.TransferDTO;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.entity.TransferEntity;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.repository.project.TransferProject;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TransferMapper {

    @Mapping(source = "userId", target = "userId")
    Transfer mapToDomain(TransferEntity entity);

    @Mapping(source = "userId", target = "userId")
    TransferEntity mapToEntity(Transfer transfer);

    @Mapping(source = "cuenta_origen_id", target = "accountOrigin.accountId")
    @Mapping(source = "nombre_o", target = "accountOrigin.name")
    @Mapping(source = "cuenta_destino_id", target = "accountDestination.accountId")
    @Mapping(source = "nombre_d", target = "accountDestination.name")
    @Mapping(source = "descripcion", target = "description")
    @Mapping(source = "fecha_creacion", target = "creationDate")
    @Mapping(source = "monto", target = "amount")
    TransferDTO mapToDomainDTO(TransferProject project);

}
