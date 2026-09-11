package com.financia.kash.movimiento.categoria.application.port.input;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface DeleteMyCategoryUseCase {
    Mono<Void> deleteMyCategory(UUID categoryId);
}
