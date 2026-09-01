package com.financia.kash.usuario.infrastructure.adapter.database.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity;
import com.financia.kash.cuenta.transferencia.infrastructure.database.entity.TransferEntity;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.entity.CategoryEntity;
import com.financia.kash.usuario.domain.model.EstadoUsuario;
import com.financia.kash.usuario.domain.model.RoleUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "nombre")
    private String name;
    @Column(name = "apellido")
    private String lastName;
    @Column(unique = true)
    private String email;
    @Column(name = "contraseña")
    private String password;
    @CreationTimestamp
    @Column(name = "fecha_creacion")
    private LocalDate creationDate;
    @Column(name = "fecha_actualizacion", nullable = true)
    private LocalDate updateDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoUsuario status;
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private RoleUser role;

    private String secret2fa;
    private boolean enable2fa;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CategoryEntity> categories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AccountEntity> accounts;

    @OneToMany(mappedBy = "user")
    private List<TransferEntity> transfers;

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
