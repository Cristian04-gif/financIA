package com.financia.kash.cuenta.transferencia.infrastructure.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.cuenta.transferencia.application.port.input.DeleteTransferUseCase;
import com.financia.kash.cuenta.transferencia.application.port.input.GetTransferUserCase;
import com.financia.kash.cuenta.transferencia.application.port.input.UpdateTransferUseCase;
import com.financia.kash.cuenta.transferencia.domain.model.Transfer;
import com.financia.kash.cuenta.transferencia.infrastructure.api.dto.TransferUpdateRequest;
import com.financia.kash.shared.domain.PaginationRequest;
import com.financia.kash.shared.domain.PaginationResponse;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@Tag(name = "Transferencias", description = "Operaciones de la API de Transferencias")
public class TransferController {

    private final GetTransferUserCase getTransferUserCase;
    private final UpdateTransferUseCase updateTransferUseCase;
    private final DeleteTransferUseCase deleteTransferUseCase;

    @Operation(summary = "Transfereencias del usuario", description = "Devuelve las transferencias del usuario logeado")
    @GetMapping("/my-transfers")
    public ResponseEntity<PaginationResponse<Transfer>> getMyTransfers(@AuthenticationPrincipal UserEntity user,
            @RequestParam(required = false, defaultValue = "0") int pageNum,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "creationDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {

        PaginationRequest paginationRequest = new PaginationRequest(pageNum, pageSize, sortBy, direction);
        PaginationResponse<Transfer> paginationResponse = getTransferUserCase.getAllMyTransfers(user.getId(),
                paginationRequest);
        return ResponseEntity.ok(paginationResponse);
    }

    @PreAuthorize("""
            hasAuthority('USER') and
                     @securityService.isOwner(#id, authentication.name, T(com.financia.kash.cuenta.transferencia.infrastructure.database.entity.TransferEntity))
                """)
    @Operation(summary = "Transfereencia del usuario", description = "Devuelve una transferencia por su id")
    @GetMapping("/my-transfers/{id}")
    public ResponseEntity<Transfer> getMyTransfer(@PathVariable UUID id) {
        return ResponseEntity.ok(getTransferUserCase.getMyTransfer(id));
    }

    @PreAuthorize("""
            hasAuthority('USER') and
                     @securityService.isOwner(#id, authentication.name, T(com.financia.kash.cuenta.transferencia.infrastructure.database.entity.TransferEntity))
                """)
    @Operation(summary = "Actualizar Transferencia", description = "Actualizar el monto o descripcion de la transferencia")
    @PutMapping("/my-transfers/{id}")
    public ResponseEntity<Transfer> updateTransfer(@PathVariable UUID id, @RequestBody TransferUpdateRequest request) {
        updateTransferUseCase.updateTransfer(id, request.newAmount(), request.newDescription());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("""
            hasAuthority('USER') and
                     @securityService.isOwner(#id, authentication.name, T(com.financia.kash.cuenta.transferencia.infrastructure.database.entity.TransferEntity))
                """)
    @Operation(summary = "Elimina una transferncia porsu id")
    @DeleteMapping("/my-transfers/{id}")
    public ResponseEntity<Transfer> delete(@PathVariable UUID id) {
        deleteTransferUseCase.deleteTransfer(id);
        return ResponseEntity.noContent().build();
    }

}
