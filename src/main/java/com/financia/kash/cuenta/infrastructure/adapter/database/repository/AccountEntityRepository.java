package com.financia.kash.cuenta.infrastructure.adapter.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financia.kash.cuenta.infrastructure.adapter.database.entity.AccountEntity;

public interface AccountEntityRepository extends JpaRepository<AccountEntity, UUID> {
    List<AccountEntity> findAllByUser_Id(UUID userId);
}
