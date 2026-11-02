package com.financia.kash.presupuesto.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.financia.kash.shared.infrastructure.utils.HasUuid;
import lombok.Data;

@Data
@Table("presupuestos")
public class BudgetEntity implements HasUuid {
    @Id
    private UUID id;
    @Column("usuario_id")
    private UUID userId;
    @Column("nombre")
    private String name;
    @Column("periodo_inicial")
    private LocalDate periodStart;
    @Column("periodo_fin")
    private LocalDate periodEnd;
    @Column("monto_limite_total")
    private BigDecimal amountLimitTotal;
    @Column("activo")
    private boolean active;
    @Column("fecha_creacion")
    private LocalDateTime creationDate;
    @Column("fecha_actualizado")
    private LocalDateTime updateDate;
}
