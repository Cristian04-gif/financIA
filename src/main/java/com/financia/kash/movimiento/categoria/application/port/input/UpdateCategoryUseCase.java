package com.financia.kash.movimiento.categoria.application.port.input;

import com.financia.kash.movimiento.categoria.application.port.input.command.UpdateCategoryCommand;
import com.financia.kash.movimiento.categoria.domain.model.Category;

import reactor.core.publisher.Mono;

public interface UpdateCategoryUseCase {
    Mono<Category> updateCategoryForUser(UpdateCategoryCommand command);

}
