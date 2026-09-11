package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.entity.MotionEntity;

public interface MovementEntityRepository extends ReactiveCrudRepository<MotionEntity, UUID> {

}
