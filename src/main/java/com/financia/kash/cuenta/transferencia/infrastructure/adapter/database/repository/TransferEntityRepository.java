package com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.entity.TransferEntity;

import reactor.core.publisher.Flux;

public interface TransferEntityRepository extends ReactiveCrudRepository<TransferEntity, UUID> {

    Flux<TransferEntity> findAllByUserId(UUID userId, Pageable pageable);
}
