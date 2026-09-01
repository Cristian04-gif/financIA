package com.financia.kash.cuenta.transferencia.infrastructure.database.mapping;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.cuenta.transferencia.infrastructure.database.entity.TransferEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TransferMapper {

    @Mapping(source = "sourceAccount.id", target = "sourceAccount")
    @Mapping(source = "user.id", target = "userId")
    Transfer mapToDomain(TransferEntity entity);

    @Mapping(source = "sourceAccount", target = "sourceAccount.id")
    @Mapping(source = "userId", target = "user")
    TransferEntity mapToEntity(Transfer transfer);

    default UserEntity mapUser(UUID userId) {
        if (userId == null) {
            return null;
        }

        UserEntity user = new UserEntity();
        user.setId(userId);
        return user;
    }

}
