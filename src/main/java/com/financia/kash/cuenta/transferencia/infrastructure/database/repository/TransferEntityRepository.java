package com.financia.kash.cuenta.transferencia.infrastructure.database.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.financia.kash.cuenta.transferencia.infrastructure.database.entity.TransferEntity;

public interface TransferEntityRepository extends JpaRepository<TransferEntity, UUID> {

    Page<TransferEntity> findAllByUserId(UUID userId, Pageable pageable);
}
