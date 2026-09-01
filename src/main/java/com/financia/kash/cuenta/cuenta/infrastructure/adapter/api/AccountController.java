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

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<List<AccountProject>> getAllMayAccounts(@AuthenticationPrincipal UserEntity user) {
        List<AccountProject> list = getAccountUseCase.getAllMyAccount(user.getId()).stream()
                .map(accountMapper::mapToProject).toList();
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("""
                hasAuthority('ADMIN') or
                (hasAuthority('USER') and
                 @securityService.isOwner(#id, authentication.name, T(com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity)))
            """)
    @Operation(summary = "Cuenta", description = "Devuelve una cuenta por su id")
    @GetMapping("/my-accounts/{id}")
    public ResponseEntity<Account> getMayAccountById(@PathVariable UUID id) {
        Account account = getAccountUseCase.getMyAccountById(id);
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Crear cuenta")
    @PostMapping("")
    public ResponseEntity<AccountProject> createAccount(@AuthenticationPrincipal UserEntity user,
            @RequestBody @Valid AccountRequest request) {
        Account account = createAccountUseCase.createAccount(user.getId(), request.name(), request.type(),
                request.initialBalance());

        return ResponseEntity.status(HttpStatus.CREATED).body(accountMapper.mapToProject(account));
    }

    @PreAuthorize("""
                hasAuthority('USER') and
                @securityService.isOwnerAccounts(
                    #request.idSource, #request.idTarget, authentication.name
                )
            """)
    @Operation(summary = "Realizar transferencia", description = "Transfiere un monto de una a otra cuenta del usuario")
    @PostMapping("/transfer")
    public ResponseEntity<Void> transferMoney(@P("request") @RequestBody @Valid TransferMoneyRequest request) {
        transferMoneyUseCase.transfer(request.idSource(), request.idTarget(), request.amount(), request.description());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("""
                hasAuthority("ADMIN") or
                (hasAuthority('USER') and
                 @securityService.isOwner(#id, authentication.name, T(com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity)))
            """)
    @Operation(summary = "Desactivar cuenta cuenta")
    @PutMapping("/my-accounts/{id}/changeStatus")
    public ResponseEntity<Void> changeStatusAccount(@PathVariable UUID id) {
        deleteAccountUseCase.changeStatusAcount(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("""
                hasAuthority('USER') and
                 @securityService.isOwner(#id, authentication.name, T(com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.entity.AccountEntity))
            """)
    @Operation(summary = "Eliminar cuenta")
    @DeleteMapping("/my-accounts/{id}")
    public ResponseEntity<Void> deleteMyAccount(@PathVariable UUID id) {
        deleteAccountUseCase.deleteMyAccount(id);
        return ResponseEntity.noContent().build();
    }

}
