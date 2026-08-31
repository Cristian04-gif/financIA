package com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.project.AccountProject;

public interface AccountEntityRepository extends JpaRepository<AccountEntity, UUID> {
    List<AccountProject> findByUserId(UUID userId);
}
