package com.financia.kash.presupuesto.infrastructure.adapter.api;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.financia.kash.presupuesto.application.port.input.*;
import com.financia.kash.presupuesto.infrastructure.adapter.api.dto.*;
import com.financia.kash.presupuesto.infrastructure.adapter.api.mapping.BudgetApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Tag(name = "Presupuestos", description = "Limites y consumo de presupuestos del usuario autenticado")
public class BudgetController {
    private final CreateBudgetUseCase createBudget;
    private final GetBudgetUseCase getBudget;
    private final UpdateBudgetUseCase updateBudget;
    private final ChangeBudgetStatusUseCase changeBudgetStatus;
    private final GetBudgetConsumptionUseCase getBudgetConsumption;
    private final BudgetApiMapper mapper;

    @PostMapping
    @Operation(summary = "Crear presupuesto con limites por categoria")
    public Mono<ResponseEntity<BudgetResponse>> create(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestBody @Valid BudgetRequest request) {
        return Mono.defer(() -> createBudget.create(userId, mapper.toCommand(request)))
                .map(mapper::toResponse)
                .map(budget -> ResponseEntity.created(URI.create("/api/v1/budgets/" + budget.id())).body(budget));
    }

    @GetMapping
    @Operation(summary = "Listar presupuestos del usuario")
    public Flux<BudgetResponse> getAll(@AuthenticationPrincipal(expression = "id") UUID userId) {
        return getBudget.getAll(userId).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar presupuesto y limites asignados")
    public Mono<BudgetResponse> getById(@AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID id) {
        return getBudget.getById(userId, id).map(mapper::toResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reemplazar configuracion y limites por categoria del presupuesto")
    public Mono<BudgetResponse> update(@AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID id, @RequestBody @Valid BudgetRequest request) {
        return Mono.defer(() -> updateBudget.update(userId, id, mapper.toCommand(request))).map(mapper::toResponse);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activar o desactivar presupuesto")
    public Mono<BudgetResponse> changeStatus(@AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID id, @RequestBody @Valid BudgetStatusRequest request) {
        return changeBudgetStatus.changeStatus(userId, id, request.active()).map(mapper::toResponse);
    }

    @GetMapping("/{id}/consumption")
    @Operation(summary = "Consultar gasto, disponible y exceso total y por categoria")
    public Mono<BudgetConsumptionResponse> getConsumption(
            @AuthenticationPrincipal(expression = "id") UUID userId, @PathVariable UUID id) {
        return getBudgetConsumption.getConsumption(userId, id).map(mapper::toResponse);
    }
}
