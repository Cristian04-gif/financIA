package com.financia.kash.shared.infrastructure.Ownership;

import java.util.UUID;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OwnershipAuthorizationManager {

    private final OwnershipAuthorizationService authorizationService;

    public ReactiveAuthorizationManager<AuthorizationContext> forResource(ResourceType resourceType) {
        return (authentication, context) -> {
            String id = (String) context.getVariables().get("id");

            if (id == null) {
                return Mono.just(new AuthorizationDecision(false));
            }

            UUID resourceId;

            try {
                resourceId = UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                return Mono.just(new AuthorizationDecision(false));
            }

            return authentication.flatMap(auth -> authorizationService.canAccess(resourceType, resourceId, auth)
                    .map(AuthorizationDecision::new));
        };
    }
}
