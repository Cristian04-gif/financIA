package com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.financia.kash.movimiento.categoria.domain.model.CategoryType;
import com.financia.kash.shared.infrastructure.utils.HasUuid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity implements HasUuid {

    @Id
    private UUID id;

    @Column(value = "usuario_id")
    private UUID userId;

    @Column(value = "nombre")
    private String name;

    @Column(value = "tipo")
    private CategoryType type;

    @Column(value = "categoria_padre_id")
    private UUID parentCategoryId;

    @Column(value = "fecha_creacion")
    private LocalDate creationDate;

    @Column(value = "fecha_actualizacion")
    private LocalDate updateDate;

    @Column(value = "activo")
    private Boolean active;

}
