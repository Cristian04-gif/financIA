package com.financia.kash.movimiento.categoria.application.port.input;

import java.util.UUID;

public interface DeleteMyCategoryUseCase {
    void deleteMyCategory(UUID categoryId);
}
