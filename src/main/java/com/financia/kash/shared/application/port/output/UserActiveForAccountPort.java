package com.financia.kash.shared.application.port.output;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface UserActiveForAccountPort {
    Mono<Boolean> isUserActive(UUID userId);
}
