package com.financia.kash.transaccion.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.financia.kash.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.transaccion.domain.model.TransactionType;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "transacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoryEntity category;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "monto", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "descripcion", length = 200)
    private String description;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDate transactionDate;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDate creationDate;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDate updateDate;
}
