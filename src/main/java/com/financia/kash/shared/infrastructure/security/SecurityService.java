package com.financia.kash.shared.infrastructure.security;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.AccountEntityRepository;
import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.repository.TransferEntityRepository;
import com.financia.kash.movimiento.movimiento.infrastructure.adapter.database.repository.MovementEntityRepository;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final UserEntityRepository userRepository;
    private final AccountEntityRepository accountRepository;
    private final TransferEntityRepository transferRepository;
    private final MovementEntityRepository movementRepository;

    public Mono<Boolean> isSameUser(UUID userId, String emaulAuth) {
        return userRepository.findById(userId).map(user -> user.getEmail().equals(emaulAuth));
    }

    public Mono<Boolean> isOwnerAccount(UUID accountId, String emailAuth) {
        return accountRepository.isOwner(accountId, emailAuth);
    }

    public Mono<Boolean> isOwnerAccounts(UUID idSource, UUID idTarget, String emailAuth) {

        Mono<Boolean> sourceAccount = accountRepository.isOwner(idSource, emailAuth);
        Mono<Boolean> targetAccount = accountRepository.isOwner(idTarget, emailAuth);

        return Mono.zip(sourceAccount, targetAccount).flatMap(tupla -> {
            boolean source = tupla.getT1();
            boolean target = tupla.getT2();

            return Mono.just(source && target ? true : false);
        });
    }

}
