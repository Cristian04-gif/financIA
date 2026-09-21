package com.financia.kash.auth.application.port.output;

import reactor.core.publisher.Mono;

public interface UserStatusUseCase {
    Mono<Boolean> isBloked(String emailUser);
}
