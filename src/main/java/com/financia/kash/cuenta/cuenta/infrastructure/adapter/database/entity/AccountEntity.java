package com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.financia.kash.cuenta.cuenta.domain.model.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    @Id
    private UUID id;

    @Column(value = "usuario_id")
    private UUID userId;

    @Column(value = "nombre")
    private String name;

    @Column(value = "tipo")
    private AccountType type;

    @Column(value = "saldo_inicial")
    private BigDecimal initialBalance;

    @Column(value = "saldo_actual")
    private BigDecimal currentBalance;

    @Column(value = "fecha_creacion")
    private LocalDate creationDate;

    @Column(value = "fecha_actualizacion")
    private LocalDate updateDate;

    @Column(value = "activo")
    private boolean active;

}
