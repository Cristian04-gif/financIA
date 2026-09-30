package com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.financia.kash.movimiento.suscripcion.domain.model.SubscriptionFrequency;
import com.financia.kash.shared.infrastructure.utils.HasUuid;

import lombok.Data;

@Table("suscripciones")
@Data
public class SubscriptionEntity implements HasUuid {

    @Id
    private UUID id;

    @Column(value = "usuario_id")
    private UUID userId;

    @Column(value = "cuenta_id")
    private UUID payingAccountId;

    @Column(value = "categoria_id")
    private UUID categoryId;

    @Column(value = "nombre")
    private String name;

    @Column(value = "monto")
    private BigDecimal amount;

    @Column(value = "frecuencia_cobro")
    private SubscriptionFrequency frequency;

    @Column(value = "dia_pago")
    private Integer payDay;

    @Column(value = "siguiente_pago")
    private LocalDate dateNextPayment;

    @Column(value = "activo")
    private Boolean active;
}
