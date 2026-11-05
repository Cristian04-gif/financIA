package com.financia.kash.auth.application.port.output;

import reactor.core.publisher.Mono;

public interface UserBlockedForAuthPort {
    Mono<Boolean> isActive(String email);
}
