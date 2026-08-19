package com.financia.kash.categoria.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Category {
    private final UUID id;
    private final UUID userId;
    private String name;
    private CategoryType type;
    private final boolean defaultCategory;
    private final LocalDate creationDate;
    private LocalDate updateDate;
    private boolean active;

    public void rename(String name) {
        this.name = name;
        markAsUpdated();
    }

    public void changeType(CategoryType type) {
        this.type = type;
        markAsUpdated();
    }

    public void deactivate() {
        this.active = false;
        markAsUpdated();
    }

    private void markAsUpdated() {
        this.updateDate = LocalDate.now();
    }
}
