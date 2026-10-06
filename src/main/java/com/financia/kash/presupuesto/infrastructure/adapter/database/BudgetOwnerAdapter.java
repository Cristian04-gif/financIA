package com.financia.kash.presupuesto.infrastructure.adapter.database;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import com.financia.kash.presupuesto.application.port.output.BudgetOwnerPort;
import com.financia.kash.usuario.domain.model.EstadoUsuario;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class BudgetOwnerAdapter implements BudgetOwnerPort {
    private final UserEntityRepository users;

    @Override
    public Mono<Boolean> isActive(UUID userId) {
        return users.findById(userId).map(user -> user.getStatus() == EstadoUsuario.ACTIVO)
                .defaultIfEmpty(false);
    }
}
