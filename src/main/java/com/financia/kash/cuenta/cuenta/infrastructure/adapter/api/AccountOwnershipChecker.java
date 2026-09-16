package com.financia.kash.cuenta.cuenta.infrastructure.adapter.api;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.AccountEntityRepository;
import com.financia.kash.shared.infrastructure.Ownership.OwnershipChecker;
import com.financia.kash.shared.infrastructure.Ownership.ResourceType;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountOwnershipChecker implements OwnershipChecker {

    private final AccountEntityRepository accountEntityRepository;

    @Override
    public ResourceType supports() {
        return ResourceType.ACCOUNT;
    }

    @Override
    public Mono<Boolean> isOwner(UUID resourceId, String userEmail) {
        return accountEntityRepository.existsByIdAndOwnerEmail(resourceId, userEmail);
    }

}
