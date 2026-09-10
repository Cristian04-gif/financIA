package com.financia.kash.auth.application.port.input;

import java.util.Map;

import reactor.core.publisher.Mono;

public interface Request2faUseCase {
    Mono<Map<String, String>> setup2fa(String email);
}
