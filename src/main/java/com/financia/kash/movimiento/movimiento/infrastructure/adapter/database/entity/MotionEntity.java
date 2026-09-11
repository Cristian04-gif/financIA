package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.financia.kash.movimiento.movimiento.domain.model.TypeMovement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("movimientos")
public class MotionEntity {

    @Id
    private UUID id;

    @Column(value = "usuario_id")
    private UUID userId;

    @Column(value = "cuenta_id")
    private UUID accountId;

    @Column(value = "categoria_id")
    private UUID categoryId;

    @Column(value = "tipo")
    private TypeMovement type;

    @Column(value = "monto")
    private BigDecimal amount;

    @Column(value = "fecha_emision")
    private LocalDate date;

    @Column(value = "descripcion")
    private String description;

    @Column(value = "comun")
    private Boolean common;

    @Column(value = "fecha_registro")
    private LocalDateTime creationDate;

    @Column(value = "fecha_actualizacion")
    private LocalDateTime updateDate;
}
