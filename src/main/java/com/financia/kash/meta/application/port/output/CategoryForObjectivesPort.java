package com.financia.kash.meta.application.port.output;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface CategoryForObjectivesPort {
    Mono<Boolean> existsById(UUID id);
}
