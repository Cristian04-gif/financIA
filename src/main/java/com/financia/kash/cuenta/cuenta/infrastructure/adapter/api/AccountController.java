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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final GetAccountUseCase getAccountUseCase;
    private final CreateAccountUseCase createAccountUseCase;
    private final TransferMoneyUseCase transferMoneyUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final AccountMapper accountMapper;

    @GetMapping("/my-accounts")
    public ResponseEntity<List<AccountProject>> getAllMayAccounts(@AuthenticationPrincipal UserEntity user) {
        List<AccountProject> list = getAccountUseCase.getAllMyAccount(user.getId()).stream()
                .map(accountMapper::mapToProject).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/my-accounts/{id}")
    public ResponseEntity<AccountProject> getMayAccountById(@PathVariable UUID accountId) {
        Account account = getAccountUseCase.getMyAccountById(accountId);
        return ResponseEntity.ok(accountMapper.mapToProject(account));
    }

    @PostMapping("")
    public ResponseEntity<AccountProject> createAccount(@RequestBody @Valid AccountRequest request) {
        Account account = createAccountUseCase.createAccount(request.userId(), request.name(), request.type(),
                request.initialBalance());

        return ResponseEntity.status(HttpStatus.CREATED).body(accountMapper.mapToProject(account));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferMoney(@RequestBody TransferMoneyRequest request) {
        transferMoneyUseCase.transfer(request.idSource(), request.idTarget(), request.amount());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/my-accounts/{id}")
    public ResponseEntity<Void> deleteMyAccount(@PathVariable UUID id) {
        deleteAccountUseCase.deleteMyAccount(id);
        return ResponseEntity.noContent().build();
    }

}
