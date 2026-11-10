package com.financia.kash.administracion.domain.model;

import java.time.LocalDate;
import java.util.UUID;
import com.financia.kash.usuario.domain.enums.RoleUser;
import com.financia.kash.usuario.domain.enums.UserStatus;

/** Proyeccion administrativa: excluye contrasena y secretos de autenticacion. */
public record AdminUser(UUID id, String name, String lastName, String email,
        RoleUser role, UserStatus status, LocalDate creationDate, LocalDate updateDate) {}
