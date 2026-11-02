package com.financia.kash.presupuesto.application.port.output;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface BudgetOwnerPort {
    Mono<Boolean> isActive(UUID userId);
}
