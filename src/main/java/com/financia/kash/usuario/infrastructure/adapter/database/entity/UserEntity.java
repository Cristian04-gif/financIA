package com.financia.kash.usuario.infrastructure.adapter.database.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.financia.kash.shared.infrastructure.utils.HasUuid;
import com.financia.kash.usuario.domain.model.EstadoUsuario;
import com.financia.kash.usuario.domain.model.RoleUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails, HasUuid {

    @Id
    private UUID id;

    @Column(value = "nombre")
    private String name;

    @Column(value = "apellido")
    private String lastName;

    @Column(value = "email")
    private String email;

    @Column(value = "contraseña")
    private String password;

    @Column(value = "fecha_creacion")
    private LocalDate creationDate;

    @Column(value = "fecha_actualizacion")
    private LocalDate updateDate;

    @Column(value = "estado")
    private EstadoUsuario status;

    @Column(value = "rol")
    private RoleUser role;

    private String secret2fa;
    private boolean enable2fa;

    public UserEntity(String name, String lastName, String email, String password, EstadoUsuario status,
            RoleUser role) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.status = status;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
