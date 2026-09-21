package com.financia.kash.auth.application.port.output;

import reactor.core.publisher.Mono;

public interface UserBlockedForAuthPort {
    Mono<Boolean> isBlocked(String email);
}
