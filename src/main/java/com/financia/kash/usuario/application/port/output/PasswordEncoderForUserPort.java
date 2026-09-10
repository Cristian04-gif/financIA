package com.financia.kash.usuario.application.port.output;

import reactor.core.publisher.Mono;

public interface PasswordEncoderForUserPort {
    Mono<String> ecoderPassword(String password);
}
