package com.financia.kash.movimiento.movimiento.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.movimiento.movimiento.application.port.input.CreateMovimentUseCase;
import com.financia.kash.movimiento.movimiento.application.port.input.DeleteMovimentUseCase;
import com.financia.kash.movimiento.movimiento.application.port.input.GetMovementUseCase;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.movimiento.movimiento.domain.model.dto.MovementDTO;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.api.dto.CreateMovementRequest;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Operaciones de la API de Movimientos")
public class MovementController {

    private final GetMovementUseCase getMovementUseCase;
    private final CreateMovimentUseCase createMovimentUseCase;
    private final DeleteMovimentUseCase deleteMovimentUseCase;

    @Operation(summary = "Movimientos del usuario", description = "Devuelve los movimientos que realizo el usuario")
    @GetMapping("/my-movements")
    public Mono<ResponseEntity<PaginationResponse<MovementDTO>>> getMyMovements(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam(required = false, defaultValue = "0") int pageNum,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "date_issue") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {
        PaginationRequest request = new PaginationRequest(pageNum, pageSize, sortBy, direction);

        return getMovementUseCase.getAllMovements(user.getId(), request).map(ResponseEntity::ok);
    }

    @Operation(summary = "Movimientos", description = "Devuelve un movimiento por su ID")
    @GetMapping("/my-movements/{id}")
    public Mono<ResponseEntity<MovementDTO>> getMovementById(@PathVariable UUID id) {
        return getMovementUseCase.getMovementById(id).map(ResponseEntity::ok);
    }

    @Operation(summary = "Movimiento", description = "Registra un movimiento del usuario")
    @PostMapping
    public Mono<ResponseEntity<Movement>> save(@AuthenticationPrincipal UserEntity user,
            @RequestBody @Valid CreateMovementRequest request) {
        return createMovimentUseCase
                .createMotion(user.getId(), request.accountId(), request.categoryId(), request.type(), request.amount(),
                        request.date(), request.description())
                .map(value -> ResponseEntity.status(HttpStatus.CREATED).body(value));
    }

    @Operation(summary = "Eliminar movimiento", description = "Elimina el movimiento restaurando el monto a la cuenta")
    @DeleteMapping("/my-movements/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable UUID id) {
        return deleteMovimentUseCase.deleteMovement(id).thenReturn(ResponseEntity.noContent().build());
    }

}
