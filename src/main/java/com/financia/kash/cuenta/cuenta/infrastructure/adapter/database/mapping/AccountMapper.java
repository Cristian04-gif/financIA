package com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.mapping;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.project.AccountProject;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccountMapper {

    @Mapping(source = "userId", target = "user")
    AccountEntity mapToEntity(Account account);

    @Mapping(source = "user.id", target = "userId")
    Account mapToDomain(AccountEntity accountEntity);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "initialBalance", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Account mapToDomain(AccountProject accountProject);

    AccountProject mapToProject(Account account);

    default UserEntity mapUser(UUID userId) {
        if (userId == null) {
            return null;
        }

        UserEntity user = new UserEntity();
        user.setId(userId);
        return user;
    }

}
