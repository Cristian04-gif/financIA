package com.financia.kash.cuenta.transferencia.infrastructure.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "transferencias")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_origen_id", nullable = false)
    private AccountEntity sourceAccount;

    @Column(name = "cuenta_destino_id", nullable = false)
    private UUID destinationAccount;

    @Column(name = "monto", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "descripcion", nullable = true)
    private String description;

    @CreationTimestamp
    @Column(name = "fecha_creacion")
    private LocalDate creationDate;
}
