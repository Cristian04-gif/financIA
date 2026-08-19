package com.financia.kash.presupuesto.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.financia.kash.categoria.infrastructure.adapter.database.entity.CategoryEntity;
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
@Table(name = "presupuestos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoryEntity category;

    @Column(name = "nombre", nullable = false, length = 80)
    private String name;

    @Column(name = "monto_limite", nullable = false, precision = 14, scale = 2)
    private BigDecimal limitAmount;

    @Column(name = "monto_gastado", nullable = false, precision = 14, scale = 2)
    private BigDecimal spentAmount;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate startDate;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDate creationDate;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDate updateDate;

    @Column(name = "activo", nullable = false)
    private boolean active = true;
}
