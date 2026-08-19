package com.financia.kash.cuenta.infrastructure.adapter.database;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.cuenta.application.port.output.AccountRepositoryPort;
import com.financia.kash.cuenta.domain.model.Account;
import com.financia.kash.cuenta.infrastructure.adapter.database.mapping.AccountMapper;
import com.financia.kash.cuenta.infrastructure.adapter.database.repository.AccountEntityRepository;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountEntityRepository accountEntityRepository;
    private final AccountMapper accountMapper;
    private final EntityManager entityManager;

    @Override
    public Account save(Account account) {
        UserEntity user = entityManager.getReference(UserEntity.class, account.getUserId());
        return accountMapper.mapToDomain(accountEntityRepository.save(accountMapper.mapToEntity(account, user)));
    }

    @Override
    public Account findById(UUID id) {
        return accountEntityRepository.findById(id)
                .map(accountMapper::mapToDomain)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + id));
    }

    @Override
    public List<Account> findByUserId(UUID userId) {
        return accountEntityRepository.findAllByUser_Id(userId).stream()
                .map(accountMapper::mapToDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        accountEntityRepository.deleteById(id);
    }
}
