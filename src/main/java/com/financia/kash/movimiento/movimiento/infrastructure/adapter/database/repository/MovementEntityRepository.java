package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.entity.MotionEntity;

public interface MovementEntityRepository extends JpaRepository<MotionEntity, UUID> {

}
