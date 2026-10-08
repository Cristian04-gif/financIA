package com.financia.kash.movimiento.categoria.application.port.input;

import com.financia.kash.movimiento.categoria.application.port.input.command.CreateCategoryUserCommand;
import com.financia.kash.movimiento.categoria.application.port.input.command.CreateMainCategoryCommand;
import com.financia.kash.movimiento.categoria.domain.model.Category;

import reactor.core.publisher.Mono;

public interface CreateCategoryUseCase {
    Mono<Category> createMainCategory(CreateMainCategoryCommand command);

    Mono<Category> createCategoryForUser(CreateCategoryUserCommand command);
}
