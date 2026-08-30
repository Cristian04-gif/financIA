package com.financia.kash.movimiento.categoria.domain.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import com.financia.kash.movimiento.categoria.domain.exception.CategoryTypeNotfoundException;
import com.financia.kash.shared.domain.utils.Default;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(onConstructor_ = { @Default })
public class Category {

    private final UUID id;
    private final UUID userId;
    private String name;
    private CategoryType type;
    private UUID parentCategoryId;
    private LocalDate creationDate;
    private LocalDate updateDate;
    private boolean active;

    public Category(String name, String type) {
        this.id = null;
        this.userId = null;
        this.name = name;
        changeType(type);
        this.parentCategoryId = null;
        this.creationDate = LocalDate.now();
        this.updateDate = null;
        this.active = true;
    }

    public Category(UUID userId, String name, String type, UUID parentCategoryId) {
        this.id = null;
        this.userId = userId;
        this.name = name;
        changeType(type);
        this.parentCategoryId = parentCategoryId;
        this.creationDate = LocalDate.now();
        this.updateDate = null;
        this.active = true;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void parentCategoryId(UUID parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public void changeType(String categoryType) {
        if (!existType(categoryType)) {
            throw new CategoryTypeNotfoundException(categoryType);
        }
        this.type = CategoryType.valueOf(categoryType.toUpperCase());
    }

    private boolean existType(String categoryType) {
        if (categoryType == null) {
            return false;
        }
        return Arrays.stream(CategoryType.values()).anyMatch(e -> e.name().equals(categoryType));
    }

    public void changeStatus(boolean status) {
        this.active = status;
    }

}
