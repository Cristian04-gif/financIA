package com.financia.kash.cuenta.cuenta.infrastructure.adapter.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.cuenta.cuenta.application.port.input.CreateAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.DeleteAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.GetAccountUseCase;
import com.financia.kash.cuenta.cuenta.application.port.input.TransferMoneyUseCase;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.dto.AccountRequest;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.api.dto.TransferMoneyRequest;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.mapping.AccountMapper;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.project.AccountProject;
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
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "Operaciones de la API de Cuentas")
public class AccountController {

    private final GetAccountUseCase getAccountUseCase;
    private final CreateAccountUseCase createAccountUseCase;
    private final TransferMoneyUseCase transferMoneyUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final AccountMapper accountMapper;

    @Operation(summary = "Cuentas del usuario", description = "Devuelve las cuentas del usuario logeado")
    @GetMapping("/my-accounts")
    public ResponseEntity<Flux<AccountProject>> getAllMyAccounts(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(getAccountUseCase.getAllMyAccount(user.getId()).map(accountMapper::mapToProject));
    }

    @Operation(summary = "Cuenta", description = "Devuelve una cuenta por su id")
    @GetMapping("/my-accounts/{id}")
    public Mono<ResponseEntity<Account>> getMyAccountById(@PathVariable UUID id) {
        return getAccountUseCase.getMyAccountById(id).map(ResponseEntity::ok);
    }

    // @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Crear cuenta")
    @PostMapping("")
    public Mono<ResponseEntity<AccountProject>> createAccount(@AuthenticationPrincipal UserEntity user,
            @RequestBody @Valid AccountRequest request) {
        return createAccountUseCase.createAccount(user.getId(), request.name(), request.type(),
                request.initialBalance())
                .map(value -> ResponseEntity.status(HttpStatus.CREATED).body(accountMapper.mapToProject(value)));

    }

    // @PreAuthorize("""
    // hasAuthority('USER') and
    // @securityService.isOwnerAccounts(
    // #request.idSource, #request.idTarget, authentication.name
    // )
    // """)
    @Operation(summary = "Realizar transferencia", description = "Transfiere un monto de una a otra cuenta del usuario")
    @PostMapping("/transfer")
    public Mono<ResponseEntity<Void>> transferMoney(@RequestBody @Valid TransferMoneyRequest request,
            @AuthenticationPrincipal UserEntity user) {
        return transferMoneyUseCase.transfer(user.getId(), request.idSource(), request.idTarget(), request.amount(),
                request.description()).thenReturn(ResponseEntity.noContent().build());
    }

    // @PreAuthorize("""
    // hasAuthority("ADMIN") or
    // (hasAuthority('USER') and
    // @securityService.isOwner(#id, authentication.name,
    // T(com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity)))
    // """)
    @Operation(summary = "Desactivar cuenta cuenta")
    @PutMapping("/my-accounts/{id}/changeStatus")
    public Mono<ResponseEntity<Void>> changeStatusAccount(@PathVariable UUID id) {
        return deleteAccountUseCase.changeStatusAcount(id).thenReturn(ResponseEntity.noContent().build());
    }

    // @PreAuthorize("""
    // hasAuthority('USER') and
    // @securityService.isOwner(#id, authentication.name,
    // T(com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity))
    // """)
    @Operation(summary = "Eliminar cuenta")
    @DeleteMapping("/my-accounts/{id}")
    public Mono<ResponseEntity<Void>> deleteMyAccount(@PathVariable UUID id) {
        return deleteAccountUseCase.deleteMyAccount(id).thenReturn(ResponseEntity.noContent().build());
    }

}
