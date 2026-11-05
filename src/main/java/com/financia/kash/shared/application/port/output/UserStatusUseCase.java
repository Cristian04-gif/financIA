package com.financia.kash.shared.application.port.output;

import reactor.core.publisher.Mono;

public interface UserStatusUseCase {
    Mono<Boolean> isActive(String emailUser);
}
