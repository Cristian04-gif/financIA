package com.financia.kash.auth.application.port.input;

import java.util.Map;

import com.financia.kash.auth.application.port.input.command.LoginUserCommand;

import reactor.core.publisher.Mono;

public interface LoginUserUseCase {
    Mono<Map<String, Object>> loginUser(LoginUserCommand command);
}
