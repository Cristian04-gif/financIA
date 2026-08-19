package com.financia.kash.cuenta.infrastructure.adapter.database.mapping;

import org.springframework.stereotype.Component;

import com.financia.kash.cuenta.domain.model.Account;
import com.financia.kash.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

@Component
public class AccountMapper {

    public AccountEntity mapToEntity(Account account, UserEntity user) {
        AccountEntity entity = new AccountEntity();
        entity.setId(account.getId());
        entity.setUser(user);
        entity.setName(account.getName());
        entity.setType(account.getType());
        entity.setInitialBalance(account.getInitialBalance());
        entity.setCurrentBalance(account.getCurrentBalance());
        entity.setCreationDate(account.getCreationDate());
        entity.setUpdateDate(account.getUpdateDate());
        entity.setActive(account.isActive());
        return entity;
    }

    public Account mapToDomain(AccountEntity entity) {
        return Account.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .name(entity.getName())
                .type(entity.getType())
                .initialBalance(entity.getInitialBalance())
                .currentBalance(entity.getCurrentBalance())
                .creationDate(entity.getCreationDate())
                .updateDate(entity.getUpdateDate())
                .active(entity.isActive())
                .build();
    }
}
