package com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.project.AccountProject;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccountMapper {

    @Mapping(source = "userId", target = "userId")
    AccountEntity mapToEntity(Account account);

    @Mapping(source = "userId", target = "userId")
    Account mapToDomain(AccountEntity accountEntity);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "initialBalance", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Account mapToDomain(AccountProject accountProject);

    AccountProject mapToProject(Account account);

}
