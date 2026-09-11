package com.financia.kash.shared.infrastructure.security;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface OwnershipChecker {
    Mono<Boolean> isOwner(UUID resourceId, String userEmail);
}
