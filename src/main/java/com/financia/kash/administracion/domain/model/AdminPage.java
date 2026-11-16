package com.financia.kash.administracion.domain.model;

import java.util.List;

public record AdminPage<T>(List<T> content, int page, int size, long totalElements, long totalPages) {
    public static <T> AdminPage<T> of(List<T> content, int page, int size, long total) {
        return new AdminPage<>(List.copyOf(content), page, size, total, (total + size - 1) / size);
    }

    public static void validate(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("page debe ser >= 0 y size debe estar entre 1 y 100");
        }
    }
}
