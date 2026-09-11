package com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.entity.TransferEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TransferMapper {

    @Mapping(source = "userId", target = "userId")
    Transfer mapToDomain(TransferEntity entity);

    @Mapping(source = "userId", target = "userId")
    TransferEntity mapToEntity(Transfer transfer);

}
