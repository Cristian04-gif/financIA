package com.financia.kash.shared.infrastructure.Ownership;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.financia.kash.usuario.domain.model.RoleUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log4j2
public class OwnershipAuthorizationService {

    private final OwnershipCheckerRegistry registry;

    public Mono<Boolean> canAccess(ResourceType resourceType, UUID resourceId, Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(RoleUser.ADMIN.name()));

        if (isAdmin) {
            return Mono.just(true);
        }
        log.info("is edmin: {}", isAdmin);
        return registry.get(resourceType).isOwner(resourceId, authentication.getName());
    }

}
