package com.financia.kash.meta.infrastructure.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.financia.kash.meta.domain.enums.ObjectiveState;
import com.financia.kash.shared.infrastructure.utils.HasUuid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("metas_financieras")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FinancialObjectiveEntity implements HasUuid {

    @Id
    private UUID id;

    @Column(value = "usuario_id")
    private UUID userId;

    @Column(value = "nombre")
    private String name;

    @Column(value = "descripcion")
    private String description;

    @Column(value = "monto_actual")
    private BigDecimal currentAmount;

    @Column(value = "monto_objetivo")
    private BigDecimal targetAmount;

    @Column(value = "progreso")
    private Double percentageProgress;

    @Column(value = "fecha_inicio")
    private LocalDate startDate;

    @Column(value = "fecha_objetivo")
    private LocalDate targetDate;

    @Column(value = "estado")
    private ObjectiveState status;

    @Column(value = "ultima_actualizacion")
    private LocalDateTime lastUpdate;
}
