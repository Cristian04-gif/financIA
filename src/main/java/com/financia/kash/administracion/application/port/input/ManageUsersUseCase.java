package com.financia.kash.administracion.application.port.input;

import java.util.UUID;
import com.financia.kash.administracion.domain.model.*;
import com.financia.kash.usuario.domain.enums.UserStatus;
import reactor.core.publisher.Mono;

public interface ManageUsersUseCase {
    Mono<AdminPage<AdminUser>> search(String search, UserStatus status, int page, int size);
    Mono<AdminUser> get(UUID id);
    Mono<AdminUser> changeStatus(UUID actor, UUID id, UserStatus status, String reason);
}
