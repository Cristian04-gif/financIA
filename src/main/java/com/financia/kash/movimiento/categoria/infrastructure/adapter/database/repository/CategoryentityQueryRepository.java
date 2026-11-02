package com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import com.financia.kash.movimiento.categoria.domain.enums.CategoryType;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.project.ProjectCategory;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class CategoryentityQueryRepository {

    private final DatabaseClient databaseClient;

    public Flux<ProjectCategory> findCategoryForVoucher(UUID userId, String name) {
        String sql = """
                (
                    SELECT *
                    FROM categorias
                    WHERE usuario_id = :userId
                      AND LOWER(nombre) = LOWER(:name)
                )
                UNION ALL
                (
                    SELECT *
                    FROM categorias
                    WHERE usuario_id IS NULL
                      AND LOWER(nombre) = LOWER(:name)
                      AND NOT EXISTS (
                          SELECT 1
                          FROM categorias
                          WHERE usuario_id = :userId
                            AND LOWER(nombre) = LOWER(:name)
                      )
                )
                UNION ALL
                (
                    SELECT *
                    FROM categorias
                    WHERE usuario_id IS NULL
                      AND categoria_padre_id IS NULL
                      AND NOT EXISTS (
                          SELECT 1
                          FROM categorias
                          WHERE usuario_id = :userId
                            AND LOWER(nombre) = LOWER(:name)
                      )
                      AND NOT EXISTS (
                          SELECT 1
                          FROM categorias
                          WHERE usuario_id IS NULL
                            AND LOWER(nombre) = LOWER(:name)
                      )
                );
                                """;

        return databaseClient.sql(sql)
                .bind("userId", userId)
                .bind("name", name)
                .map((row, metadata) -> {
                    UUID id = row.get("id", UUID.class);
                    UUID userIdRes = row.get("usuario_id", UUID.class);
                    String nameRes = row.get("nombre", String.class);
                    CategoryType type = row.get("tipo", CategoryType.class);
                    LocalDate creationDate = row.get("fecha_creacion", LocalDate.class);
                    boolean active = row.get("activo", boolean.class);

                    return new ProjectCategory(id, userIdRes, nameRes, type, creationDate, active);
                }).all();
    }
}
