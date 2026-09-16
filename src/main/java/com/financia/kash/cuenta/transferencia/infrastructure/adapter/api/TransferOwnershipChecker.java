package com.financia.kash.cuenta.transferencia.infrastructure.adapter.api;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financia.kash.cuenta.transferencia.infrastructure.adapter.database.repository.TransferEntityRepository;
import com.financia.kash.shared.infrastructure.Ownership.OwnershipChecker;
import com.financia.kash.shared.infrastructure.Ownership.ResourceType;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TransferOwnershipChecker implements OwnershipChecker {

    private final TransferEntityRepository entityRepository;

    @Override
    public ResourceType supports() {
        return ResourceType.TRANSFER;
    }

    @Override
    public Mono<Boolean> isOwner(UUID resourceId, String userEmail) {
        return entityRepository.existsByIdAndOwnerEmail(resourceId, userEmail);
    }

}
