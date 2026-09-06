package com.financia.kash.movimiento.categoria.application.service;

import java.util.List;
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

@Service
@RequiredArgsConstructor
public class CategoryService
        implements GetCategoriesUseCase, CreateCategoryUseCase, UpdateCategoryUseCase, DeleteMyCategoryUseCase,
        ChangeStatusUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final UserForCategoryPort userForCategoryPort;

    @Override
    public List<Category> getGlobalCategories() {
        return categoryRepositoryPort.findGlobalCategories();
    }

    @Override
    public List<Category> getAllMyCategory(UUID userId) {
        return categoryRepositoryPort.findAllMyCategories(userId);

    }

    @Override
    public Category getById(UUID id) {
        return categoryRepositoryPort.findById(id);
    }

    @Override
    public Category createMainCategory(String name, String type) {
        Category category = new Category(name, type);
        return categoryRepositoryPort.save(category);

    }

    @Override
    public Category createCategoryForUser(UUID userId, String name, String type, UUID parentCategoryId) {
        Category categoryParent = getById(parentCategoryId);
        User user = userForCategoryPort.findUserById(userId);
        Category category = new Category(user.getId(), name, type, categoryParent.getId());
        return categoryRepositoryPort.save(category);
    }

    @Override
    public void deleteMyCategory(UUID categoryId) {
        categoryRepositoryPort.delete(categoryId);

    }

    @Override
    public Category updateCategoryForUser(UUID id, String name, String type, UUID parentId, boolean active) {

        Category category = getById(id);
        Category categoryParent = getById(parentId);

        category.rename(name);
        category.parentCategoryId(categoryParent.getId());
        category.changeType(type);
        category.changeStatus(active);

        return categoryRepositoryPort.save(category);
    }

    @Override
    public void changeStatus(UUID id) {
        Category category = categoryRepositoryPort.findById(id);
        category.changeStatus(!category.isActive());

    }

}
