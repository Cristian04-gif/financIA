package com.financia.kash.presupuesto.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.financia.kash.shared.infrastructure.utils.HasUuid;
import lombok.Data;

@Data
@Table("presupuesto_categorias")
public class BudgetCategoryEntity implements HasUuid {
    @Id
    private UUID id;
    @Column("presupuesto_id")
    private UUID budgetId;
    @Column("categoria_id")
    private UUID categoryId;
    @Column("monto_limite")
    private BigDecimal amountLimit;
}

