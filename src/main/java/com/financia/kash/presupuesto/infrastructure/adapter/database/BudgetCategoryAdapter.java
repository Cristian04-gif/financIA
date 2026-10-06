package com.financia.kash.presupuesto.infrastructure.adapter.database;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import com.financia.kash.movimiento.categoria.domain.model.CategoryType;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryEntityRepository;
import com.financia.kash.presupuesto.application.model.BudgetCategoryInfo;
import com.financia.kash.presupuesto.application.port.output.BudgetCategoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class BudgetCategoryAdapter implements BudgetCategoryPort {
    private final CategoryEntityRepository categories;

    @Override
    public Mono<BudgetCategoryInfo> findById(UUID categoryId) {
        return categories.findById(categoryId).map(category -> new BudgetCategoryInfo(
                category.getId(), category.getUserId(), category.getType() == CategoryType.GASTOS,
                Boolean.TRUE.equals(category.getActive())));
    }
}

