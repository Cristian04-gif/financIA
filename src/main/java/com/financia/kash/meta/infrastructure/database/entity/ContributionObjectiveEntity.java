package com.financia.kash.meta.infrastructure.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.financia.kash.shared.infrastructure.utils.HasUuid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("aportes_meta")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContributionObjectiveEntity implements HasUuid {

    @Id
    private UUID id;

    @Column(value = "meta_id")
    private UUID objectiveId;

    @Column(value = "movimiento_id")
    private UUID movementId;

    @Column(value = "monto")
    private BigDecimal amount;

    @Column(value = "fecha")
    private LocalDate date;

    @Column(value = "descripcion")
    private String description;
}
