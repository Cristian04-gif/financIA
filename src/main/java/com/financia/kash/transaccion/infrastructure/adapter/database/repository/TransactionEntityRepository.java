package com.financia.kash.transaccion.infrastructure.adapter.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financia.kash.transaccion.infrastructure.adapter.database.entity.TransactionEntity;

public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByUser_Id(UUID userId);
}
