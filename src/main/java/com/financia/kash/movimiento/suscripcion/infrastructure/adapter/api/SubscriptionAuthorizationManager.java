package com.financia.kash.movimiento.suscripcion.infrastructure.adapter.api;

import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;

import com.financia.kash.auth.application.port.output.UserStatusUseCase;
import com.financia.kash.movimiento.suscripcion.infrastructure.adapter.database.repository.SubscriptionEntityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SubscriptionAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final SubscriptionEntityRepository subscriptionEntityRepository;
    private final UserStatusUseCase userStatusUseCase;

    @Override
    public Mono<AuthorizationResult> authorize(Mono<Authentication> authentication,
            @Nullable AuthorizationContext object) {
        String subIdStrig = (String) object.getVariables().get("id");

        if (subIdStrig == null) {
            return Mono.just(new AuthorizationDecision(false));
        }

        UUID subId;
        try {
            subId = UUID.fromString(subIdStrig);
        } catch (IllegalArgumentException e) {
            return Mono.just(new AuthorizationDecision(false));
        }

        return authentication.flatMap(auth -> {
            String emailUser = auth.getName();

            return userStatusUseCase.isBloked(emailUser).flatMap(blocked -> {
                if (!blocked) {
                    return Mono.just(new AuthorizationDecision(false));
                }
                if (isAdmin(auth)) {
                    return Mono.just(new AuthorizationDecision(true));
                }

                return subscriptionEntityRepository.existsByIdAndOwnerEmail(subId, emailUser)
                        .map(AuthorizationDecision::new);
            });
        });
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }
}
