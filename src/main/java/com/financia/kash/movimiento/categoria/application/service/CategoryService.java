package com.financia.kash.movimiento.categoria.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.movimiento.categoria.application.port.input.ChangeStatusUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.CreateCategoryUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.DeleteMyCategoryUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.GetCategoriesUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.UpdateCategoryUseCase;
import com.financia.kash.movimiento.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.movimiento.categoria.application.port.output.UserForCategoryPort;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.usuario.domain.model.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryService
        implements GetCategoriesUseCase, CreateCategoryUseCase, UpdateCategoryUseCase, DeleteMyCategoryUseCase,
        ChangeStatusUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final UserForCategoryPort userForCategoryPort;

    @Override
    public Flux<Category> getGlobalCategories() {
        return categoryRepositoryPort.findGlobalCategories();
    }

    @Override
    public Flux<Category> getAllMyCategory(UUID userId) {
        return categoryRepositoryPort.findAllMyCategories(userId);

    }

    @Override
    public Mono<Category> getById(UUID id) {
        return categoryRepositoryPort.findById(id);
    }

    @Override
    public Mono<Category> createMainCategory(String name, String type) {
        return Mono.just(new Category(name, type)).flatMap(categoryRepositoryPort::save);

    }

    @Override
    public Mono<Category> createCategoryForUser(UUID userId, String name, String type, UUID parentCategoryId) {
        Mono<Category> categoryParentMono = getById(parentCategoryId);
        Mono<User> userMono = userForCategoryPort.findUserById(userId);

        return Mono.zip(categoryParentMono, userMono).flatMap(tuple -> {
            Category categoryParent = tuple.getT1();
            User user = tuple.getT2();

            Category category = new Category(user.getId(), name, type, categoryParent.getId());
            return categoryRepositoryPort.save(category);

        });

    }

    @Override
    public Mono<Void> deleteMyCategory(UUID categoryId) {
        return categoryRepositoryPort.findById(categoryId)
                .flatMap(category -> categoryRepositoryPort.delete(categoryId));
    }

    @Override
    public Mono<Category> updateCategoryForUser(UUID id, String name, String type, UUID parentId, boolean active) {

        Mono<Category> categoryMono = getById(id);
        Mono<Category> categoryParentMono = getById(parentId);

        return Mono.zip(categoryMono, categoryParentMono).flatMap(tuple -> {
            Category category = tuple.getT1();
            Category categoryParent = tuple.getT2();

            category.rename(name);
            category.parentCategoryId(categoryParent.getId());
            category.changeType(type);
            category.changeStatus(active);

            return categoryRepositoryPort.save(category);
        });

    }

    @Override
    public Mono<Void> changeStatus(UUID id) {
        return categoryRepositoryPort.findById(id).flatMap(category -> {
            category.changeStatus(!category.isActive());
            return categoryRepositoryPort.save(category);
        }).then();

    }

}
