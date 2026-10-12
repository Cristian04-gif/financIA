package com.financia.kash.movimiento.suscripcion.application.port.input;

import reactor.core.publisher.Mono;

public interface IncreaseFundsUseCase {
    Mono<Void> increaseFunds();
}
