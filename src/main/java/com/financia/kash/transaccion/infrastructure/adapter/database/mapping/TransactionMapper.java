package com.financia.kash.transaccion.infrastructure.adapter.database.mapping;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.transaccion.domain.model.Transaction;
import com.financia.kash.transaccion.infrastructure.adapter.database.entity.TransactionEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

@Component
public class TransactionMapper {

    public TransactionEntity mapToEntity(Transaction transaction, UserEntity user, AccountEntity account,
            CategoryEntity category) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(transaction.getId());
        entity.setUser(user);
        entity.setAccount(account);
        entity.setCategory(category);
        entity.setType(transaction.getType());
        entity.setAmount(transaction.getAmount());
        entity.setDescription(transaction.getDescription());
        entity.setTransactionDate(transaction.getTransactionDate());
        entity.setCreationDate(transaction.getCreationDate());
        entity.setUpdateDate(transaction.getUpdateDate());
        return entity;
    }

    public Transaction mapToDomain(TransactionEntity entity) {
        UUID categoryId = entity.getCategory() == null ? null : entity.getCategory().getId();
        return Transaction.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .accountId(entity.getAccount().getId())
                .categoryId(categoryId)
                .type(entity.getType())
                .amount(entity.getAmount())
                .description(entity.getDescription())
                .transactionDate(entity.getTransactionDate())
                .creationDate(entity.getCreationDate())
                .updateDate(entity.getUpdateDate())
                .build();
    }
}
