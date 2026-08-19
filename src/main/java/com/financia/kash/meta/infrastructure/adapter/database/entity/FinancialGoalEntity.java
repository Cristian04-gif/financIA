package com.financia.kash.meta.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.financia.kash.meta.domain.model.GoalStatus;
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
@Table(name = "metas_financieras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialGoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UserEntity user;

    @Column(name = "nombre", nullable = false, length = 80)
    private String name;

    @Column(name = "monto_objetivo", nullable = false, precision = 14, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "monto_actual", nullable = false, precision = 14, scale = 2)
    private BigDecimal currentAmount;

    @Column(name = "fecha_objetivo", nullable = false)
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private GoalStatus status;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDate creationDate;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDate updateDate;
}
