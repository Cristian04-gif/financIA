package com.financia.kash.administracion.application.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.financia.kash.administracion.application.port.input.ManageUsersUseCase;
import com.financia.kash.administracion.application.port.output.AdminUserPort;
import com.financia.kash.administracion.domain.exception.*;
import com.financia.kash.administracion.domain.model.*;
import com.financia.kash.usuario.domain.enums.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserAdminService implements ManageUsersUseCase {
    private final AdminUserPort users;

    public Mono<AdminPage<AdminUser>> search(String search, UserStatus status, int page, int size) {
        return Mono.defer(() -> {
            AdminPage.validate(page, size);
            String term = search == null ? "" : search.strip();
            if (term.length() > 255) throw new IllegalArgumentException("La busqueda admite hasta 255 caracteres");
            return users.search(term, status, page, size).collectList()
                    .zipWith(users.count(term, status))
                    .map(result -> AdminPage.of(result.getT1(), page, size, result.getT2()));
        });
    }

    public Mono<AdminUser> get(UUID id) {
        return users.find(id).switchIfEmpty(Mono.error(new AdminUserNotFoundException(id)));
    }

    public Mono<AdminUser> changeStatus(UUID actor, UUID id, UserStatus status, String reason) {
        return Mono.defer(() -> {
            if (reason == null || reason.isBlank() || reason.strip().length() > 500) {
                throw new IllegalArgumentException("El motivo es obligatorio y admite hasta 500 caracteres");
            }
            if (actor == null || actor.equals(id)) {
                throw new AdminConflictException("No puedes cambiar tu propio estado");
            }
            if (status != UserStatus.ACTIVO && status != UserStatus.SUSPENDIDO) {
                throw new IllegalArgumentException("Solo se permite ACTIVO o SUSPENDIDO");
            }
            return get(id).flatMap(user -> {
                if (user.role() != RoleUser.USER || (user.status() != UserStatus.ACTIVO
                        && user.status() != UserStatus.SUSPENDIDO)) {
                    return Mono.error(new AdminConflictException("Solo se gestionan usuarios USER activos o suspendidos"));
                }
                return users.changeStatus(id, status)
                        .switchIfEmpty(Mono.error(new AdminConflictException("El estado o rol del usuario ha cambiado")))
                        .flatMap(saved -> users.invalidateCache(saved).thenReturn(saved));
            });
        });
    }
}
