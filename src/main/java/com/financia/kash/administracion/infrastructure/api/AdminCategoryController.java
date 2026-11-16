package com.financia.kash.administracion.infrastructure.api;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.financia.kash.administracion.application.port.input.ManageGlobalCategoriesUseCase;
import com.financia.kash.movimiento.categoria.domain.enums.CategoryType;
import com.financia.kash.movimiento.categoria.domain.model.Category;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Administracion - Categorias")
public class AdminCategoryController {
    private final ManageGlobalCategoriesUseCase categories;

    public record CreateCategory(@NotBlank @Size(max = 255) String name, @NotNull CategoryType type) {}
    public record RenameCategory(@NotBlank @Size(max = 255) String name) {}
    public record CategoryStatus(@NotNull Boolean active) {}

    @GetMapping
    public Flux<Category> list() { return categories.list(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Category> create(@AuthenticationPrincipal(expression = "id") UUID actor,
            @Valid @RequestBody CreateCategory request) {
        return categories.create(actor, request.name(), request.type());
    }

    @PutMapping("/{id}")
    public Mono<Category> rename(@AuthenticationPrincipal(expression = "id") UUID actor,
            @PathVariable UUID id, @Valid @RequestBody RenameCategory request) {
        return categories.rename(actor, id, request.name());
    }

    @PatchMapping("/{id}/status")
    public Mono<Category> status(@AuthenticationPrincipal(expression = "id") UUID actor,
            @PathVariable UUID id, @Valid @RequestBody CategoryStatus request) {
        return categories.changeStatus(actor, id, request.active());
    }
}
