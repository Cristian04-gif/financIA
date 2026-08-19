package com.financia.kash.transaccion.infrastructure.adapter.database;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.transaccion.application.port.output.TransactionRepositoryPort;
import com.financia.kash.transaccion.domain.model.Transaction;
import com.financia.kash.transaccion.infrastructure.adapter.database.mapping.TransactionMapper;
import com.financia.kash.transaccion.infrastructure.adapter.database.repository.TransactionEntityRepository;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionEntityRepository transactionEntityRepository;
    private final TransactionMapper transactionMapper;
    private final EntityManager entityManager;

    @Override
    public Transaction save(Transaction transaction) {
        UserEntity user = entityManager.getReference(UserEntity.class, transaction.getUserId());
        AccountEntity account = entityManager.getReference(AccountEntity.class, transaction.getAccountId());
        CategoryEntity category = transaction.getCategoryId() == null
                ? null
                : entityManager.getReference(CategoryEntity.class, transaction.getCategoryId());
        return transactionMapper.mapToDomain(transactionEntityRepository.save(transactionMapper.mapToEntity(transaction, user, account, category)));
    }

    @Override
    public Transaction findById(UUID id) {
        return transactionEntityRepository.findById(id)
                .map(transactionMapper::mapToDomain)
                .orElseThrow(() -> new IllegalArgumentException("Transaccion no encontrada: " + id));
    }

    @Override
    public List<Transaction> findByUserId(UUID userId) {
        return transactionEntityRepository.findAllByUser_Id(userId).stream()
                .map(transactionMapper::mapToDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        transactionEntityRepository.deleteById(id);
    }
}
