package com.financia.kash.movimiento.comprobante.application.port.output;

import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.model.Category;

import reactor.core.publisher.Flux;

public interface CategoryForVoucherPort {
    Flux<Category> findCategoryByNameAndUserId(UUID userId, String name);

    Flux<Category> findAllGlobalsAndByuserId(UUID userId);
}
