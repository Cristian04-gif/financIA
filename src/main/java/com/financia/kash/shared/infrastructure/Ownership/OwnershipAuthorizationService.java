package com.financia.kash.shared.infrastructure.Ownership;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OwnershipAuthorizationService {

    private final OwnershipCheckerRegistry registry;

    public Mono<Boolean> canAccess(ResourceType resourceType, UUID resourceId, Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            return Mono.just(true);
        }

        return registry.get(resourceType).isOwner(resourceId, authentication.getName());
    }

}
