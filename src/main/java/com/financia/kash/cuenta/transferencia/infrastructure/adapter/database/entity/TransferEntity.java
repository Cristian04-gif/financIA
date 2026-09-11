package com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("transferencias")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferEntity {

    @Id
    private UUID id;

    @Column(value = "usuario_id")
    private UUID userId;

    @Column(value = "cuenta_origen_id")
    private UUID sourceAccount;

    @Column(value = "cuenta_destino_id")
    private UUID destinationAccount;

    @Column(value = "monto")
    private BigDecimal amount;

    @Column(value = "descripcion")
    private String description;

    @Column(value = "fecha_creacion")
    private LocalDate creationDate;

}
