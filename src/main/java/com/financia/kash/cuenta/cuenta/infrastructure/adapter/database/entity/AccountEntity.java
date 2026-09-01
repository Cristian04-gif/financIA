package com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.financia.kash.cuenta.cuenta.domain.model.AccountType;
import com.financia.kash.cuenta.transferencia.infrastructure.database.entity.TransferEntity;
import com.financia.kash.shared.infrastructure.security.Ownable;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity implements Ownable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UserEntity user;

    @Column(name = "nombre", nullable = false, length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private AccountType type;

    @Column(name = "saldo_inicial", nullable = false, precision = 14, scale = 2)
    private BigDecimal initialBalance;

    @Column(name = "saldo_actual", nullable = false, precision = 14, scale = 2)
    private BigDecimal currentBalance;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDate creationDate;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDate updateDate;

    @Column(name = "activo", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.REMOVE)
    private List<TransferEntity> transfers;

    @Override
    public String getOwnerEmail() {
        if (user == null) {
            return null;
        }
        return user.getEmail();
    }

}
