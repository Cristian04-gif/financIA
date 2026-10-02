package com.financia.kash.movimiento.suscripcion.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.movimiento.suscripcion.application.port.input.ChangeStatusSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.CreateSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.application.port.input.GetSubscriptionUseCase;
import com.financia.kash.movimiento.suscripcion.domain.model.Subscription;
import com.financia.kash.movimiento.suscripcion.infrastructure.adapter.api.dto.CreateSubscriptionRequest;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Suscripciones", description = "Operaciones de la API de Suscripciones")
public class SubscriptionController {

    private final GetSubscriptionUseCase getSubscriptionUseCase;
    private final CreateSubscriptionUseCase createSubscriptionUseCase;
    private final ChangeStatusSubscriptionUseCase changeStatusSubscriptionUseCase;

    @Operation(summary = "Suscripciones del usuario", description = "Devuelve las suscripciones del usuario")
    @GetMapping("/my-subs")
    public ResponseEntity<Flux<Subscription>> getMySubscription(@AuthenticationPrincipal UserEntity user) {
        Flux<Subscription> flux = getSubscriptionUseCase.getSubscriptionForUser(user.getId());
        return ResponseEntity.ok(flux);
    }

    @Operation(summary = "Suscripcion", description = "Devuelve una suscripcion por su ID")
    @GetMapping("/my-subs/{id}")
    public Mono<ResponseEntity<Subscription>> getMySubById(@PathVariable UUID id) {
        return getSubscriptionUseCase.getById(id).map(ResponseEntity::ok);
    }

    @Operation(summary = "Suscripciones", description = "Registra una suscripcion del usuario")
    @PostMapping
    public Mono<ResponseEntity<Subscription>> save(@AuthenticationPrincipal UserEntity user,
            @RequestBody @Valid CreateSubscriptionRequest request) {
        return createSubscriptionUseCase.create(user.getId(), request.payingAccountId(), request.categoryId(),
                request.name(), request.amount(), request.frequency(), request.payDay())
                .map(value -> ResponseEntity.status(HttpStatus.CREATED).body(value));
    }

    @Operation(summary = "Actualizar el estado de la suscripcion", description = "Actualiza el estado de la suscripcion")
    @PutMapping("/my-subs/{id}")
    public Mono<ResponseEntity<Void>> changeStatus(@PathVariable UUID id) {
        return changeStatusSubscriptionUseCase.changeStatusSubscription(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

}
