package com.financia.kash.shared.infrastructure.Ownership;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface OwnershipChecker {
    ResourceType supports();

    Mono<Boolean> isOwner(UUID resourceId, String userEmail);
}
