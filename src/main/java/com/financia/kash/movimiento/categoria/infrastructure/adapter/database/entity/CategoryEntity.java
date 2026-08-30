package com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.financia.kash.movimiento.categoria.domain.model.CategoryType;
import com.financia.kash.shared.infrastructure.security.Ownable;
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
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity implements Ownable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "usuario_id", nullable = true)
    private UserEntity user;

    @Column(name = "nombre")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private CategoryType type;

    @Column(name = "categoria_padre_id", nullable = true)
    private UUID parentCategoryId;

    @Column(name = "fecha_creacion")
    @CreationTimestamp
    private LocalDate creationDate;

    @Column(name = "fecha_actualizacion", nullable = true)
    private LocalDate updateDate;

    @Column(name = "activo")
    private boolean active;

    @Override
    public String getOwnerEmail() {
        if (user == null) {
            return "global";
        }
        return this.user.getEmail();
    }
}
