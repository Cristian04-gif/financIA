package com.financia.kash.movimiento.categoria.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.financia.kash.movimiento.categoria.application.port.input.ChangeStatusUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.CreateCategoryUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.DeleteMyCategoryUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.GetCategoriesUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.UpdateCategoryUseCase;
import com.financia.kash.movimiento.categoria.application.port.input.command.CreateCategoryUserCommand;
import com.financia.kash.movimiento.categoria.application.port.input.command.CreateMainCategoryCommand;
import com.financia.kash.movimiento.categoria.application.port.input.command.UpdateCategoryCommand;
import com.financia.kash.movimiento.categoria.application.port.output.CategoryRepositoryPort;
import com.financia.kash.movimiento.categoria.application.port.output.UserForCategoryPort;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import com.financia.kash.shared.application.service.CacheService;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;
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
    private final CacheService<Category> cacheService;

    @Override
    public Flux<Category> getGlobalCategories() {
        String cacheKey = CacheKeys.allGlobalCategories();
        return cacheService.getCacheList(cacheKey, Category.class)
                .switchIfEmpty(categoryRepositoryPort.findGlobalCategories().collectList()
                        .flatMapMany(categories -> cacheService.saveListCache(cacheKey, categories, 60)));
    }

    @Override
    public Flux<Category> getAllMyCategory(UUID userId) {
        String cacheKey = CacheKeys.allUserCategories(userId);
        return cacheService.getCacheList(cacheKey, Category.class)
                .switchIfEmpty(categoryRepositoryPort.findAllMyCategories(userId).collectList()
                        .flatMapMany(categories -> cacheService.saveListCache(cacheKey, categories, 60)));
    }

    @Override
    public Mono<Category> getById(UUID id) {
        String cacheKey = CacheKeys.category(id);
        return cacheService.retrieveMonoCache(cacheKey, Category.class)
                .switchIfEmpty(categoryRepositoryPort.findById(id))
                .flatMap(category -> cacheService.saveMonoCache(cacheKey, category, 60));
    }

    @Override
    public Mono<Category> createMainCategory(CreateMainCategoryCommand command) {
        return Mono.just(new Category(command.name(), command.type())).flatMap(categoryRepositoryPort::save)
                .flatMap(category -> CacheKeys.deleteAllGlobalCategories(cacheService.redisTemplate())
                        .thenReturn(category));
    }

    @Override
    public Mono<Category> createCategoryForUser(CreateCategoryUserCommand command) {
        Mono<Category> categoryParentMono = getById(command.parentCategoryId());
        Mono<User> userMono = userForCategoryPort.findUserById(command.userId());

        return Mono.zip(categoryParentMono, userMono).flatMap(tuple -> {
            Category categoryParent = tuple.getT1();
            User user = tuple.getT2();

            Category category = new Category(user.getId(), command.name(), command.type(), categoryParent.getId());
            return categoryRepositoryPort.save(category);
        }).flatMap(category -> CacheKeys.deleteAllUserCategories(cacheService.redisTemplate(), command.userId())
                .thenReturn(category));

    }

    @Override
    public Mono<Void> deleteMyCategory(UUID categoryId) {
        return categoryRepositoryPort.findById(categoryId)
                .flatMap(category -> categoryRepositoryPort.delete(categoryId).then(CacheKeys
                        .clearAllUserCategories(cacheService.redisTemplate(), category.getUserId(), categoryId)))
                .then();
    }

    @Override
    public Mono<Category> updateCategoryForUser(UpdateCategoryCommand command) {

        Mono<Category> categoryMono = getById(command.id());
        Mono<Category> categoryParentMono = getById(command.parentId());

        return Mono.zip(categoryMono, categoryParentMono).flatMap(tuple -> {
            Category category = tuple.getT1();
            Category categoryParent = tuple.getT2();

            category.rename(command.name());
            category.parentCategoryId(categoryParent.getId());
            category.changeType(command.type());
            category.changeStatus(command.active());

            return categoryRepositoryPort.save(category);
        }).flatMap(category -> CacheKeys.clearAllUserCategories(cacheService.redisTemplate(), category.getUserId(),
                category.getId()).thenReturn(category));

    }

    @Override
    public Mono<Void> changeStatus(UUID id) {
        return categoryRepositoryPort.findById(id).flatMap(category -> {
            category.changeStatus(!category.isActive());
            return categoryRepositoryPort.save(category);
        }).flatMap(category -> category.getUserId().equals(null)
                ? CacheKeys.clearAllGlobalCategories(cacheService.redisTemplate(), id)
                : CacheKeys.clearAllUserCategories(cacheService.redisTemplate(), id, category.getUserId())).then();
    }

}
