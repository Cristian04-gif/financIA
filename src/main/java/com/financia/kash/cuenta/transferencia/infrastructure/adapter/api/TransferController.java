package com.financia.kash.cuenta.transferencia.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.cuenta.transferencia.application.port.input.DeleteTransferUseCase;
import com.financia.kash.cuenta.transferencia.application.port.input.GetTransferUserCase;
import com.financia.kash.cuenta.transferencia.application.port.input.UpdateTransferUseCase;
import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.cuenta.transferencia.domain.model.TransferDTO;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.api.dto.TransferUpdateRequest;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/transfers/my-transfers")
@RequiredArgsConstructor
@Tag(name = "Transferencias", description = "Operaciones de la API de Transferencias")
public class TransferController {

    private final GetTransferUserCase getTransferUserCase;
    private final UpdateTransferUseCase updateTransferUseCase;
    private final DeleteTransferUseCase deleteTransferUseCase;

    @Operation(summary = "Transfereencias del usuario", description = "Devuelve las transferencias del usuario logeado")
    @GetMapping()
    public Mono<ResponseEntity<PaginationResponse<Transfer>>> getMyTransfers(@AuthenticationPrincipal UserEntity user,
            @RequestParam(required = false, defaultValue = "0") int pageNum,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "creationDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {

        PaginationRequest paginationRequest = new PaginationRequest(pageNum, pageSize, sortBy, direction);
        return getTransferUserCase.getAllMyTransfers(user.getId(),
                paginationRequest).map(ResponseEntity::ok);
    }

    @Operation(summary = "Transfereencias por cuenta de usuario", description = "Devuelve las transferencias de una cuenta especifica del usuario")
    @GetMapping("/account/{id}")
    public Mono<ResponseEntity<PaginationResponse<TransferDTO>>> getmyTransferByAccount(@PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "0") int pageNum,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "fecha_creacion") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {
        PaginationRequest paginationRequest = new PaginationRequest(pageNum, pageSize, sortBy, direction);
        return getTransferUserCase.getAllTransfersByAccount(id, paginationRequest).map(ResponseEntity::ok);
    }

    @Operation(summary = "Transfereencia del usuario", description = "Devuelve una transferencia por su id")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Transfer>> getMyTransfer(@PathVariable UUID id) {
        return getTransferUserCase.getMyTransfer(id).map(ResponseEntity::ok);
    }

    @Operation(summary = "Actualizar Transferencia", description = "Actualizar el monto o descripcion de la transferencia")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Transfer>> updateTransfer(@PathVariable UUID id,
            @RequestBody TransferUpdateRequest request) {
        return updateTransferUseCase.updateTransfer(id, request.newAmount(), request.newDescription())
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Elimina una transferncia porsu id")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Transfer>> delete(@PathVariable UUID id) {
        return deleteTransferUseCase.deleteTransfer(id).thenReturn(ResponseEntity.noContent().build());
    }

}
