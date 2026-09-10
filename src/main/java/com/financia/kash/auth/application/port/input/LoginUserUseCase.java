package com.financia.kash.auth.application.port.input;

import java.util.Map;

import reactor.core.publisher.Mono;

public interface LoginUserUseCase {
    Mono<Map<String, Object>> loginUser(String email, String password);
}
