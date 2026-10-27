package com.financia.kash.administracion.application.port.output;

import java.util.UUID;
import com.financia.kash.administracion.domain.model.AdminUser;
import com.financia.kash.usuario.domain.enums.UserStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminUserPort {
    Flux<AdminUser> search(String search, UserStatus status, int page, int size);
    Mono<Long> count(String search, UserStatus status);
    Mono<AdminUser> find(UUID id);
    Mono<AdminUser> changeStatus(UUID id, UserStatus status);
    Mono<Void> invalidateCache(AdminUser user);
}
