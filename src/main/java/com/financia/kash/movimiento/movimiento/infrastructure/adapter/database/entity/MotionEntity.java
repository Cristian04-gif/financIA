package com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.movimiento.movimiento.domain.model.TypeMovement;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movimientos")
public class MotionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "cuenta_id")
    private AccountEntity account;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoryEntity category;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TypeMovement type;

    @Column(name = "monto", precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "fecha_emision")
    private LocalDate date;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "comun")
    private Boolean common;

    @Column(name = "fecha_registro")
    private LocalDateTime creationDate;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime updateDate;
}
