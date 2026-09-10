package com.financia.kash.auth.application.port.input;

import java.util.Map;

import reactor.core.publisher.Mono;

public interface Confirm2FARequestUseCase {
    Mono<String> confirm2fa(String emailUser, Map<String, String> request);
}
