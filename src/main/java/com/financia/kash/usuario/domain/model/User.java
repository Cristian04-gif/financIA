package com.financia.kash.usuario.domain.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import com.financia.kash.usuario.domain.exception.User2FAEnabledException;
import com.financia.kash.usuario.domain.exception.UserStateNotFoundException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class User {
    private final UUID id;
    private final String name;
    private final String lastName;
    private final String email;
    private String password;
    private final LocalDate creationDate;
    private LocalDate updateDate;
    private EstadoUsuario status;
    private RoleUser role;
    private String secret2fa;
    private boolean enable2fa;

    private boolean existStatus(String estado) {
        if (estado == null)
            return false;
        return Arrays.stream(EstadoUsuario.values()).anyMatch(e -> e.name().equalsIgnoreCase(estado));
    }

    public void changeStatus(String estado) {
        if (!existStatus(estado)) {
            throw new UserStateNotFoundException(estado);
        }
        this.status = EstadoUsuario.valueOf(estado.toUpperCase());
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void userUpdateDate() {
        this.updateDate = LocalDate.now();
    }

    public void setSecret2fa(String secret) {
        if (this.enable2fa) {
            throw new User2FAEnabledException(this.id);
        }
        this.secret2fa = secret;
    }

    public void enable2fa() {
        if (this.enable2fa) {
            throw new User2FAEnabledException(this.id);
        }
        this.enable2fa = true;
    }

}
