package com.financia.kash.cuenta.cuenta.infrastructure.adapter.api;

import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;

import com.financia.kash.auth.application.port.output.UserStatusUseCase;
import com.financia.kash.cuenta.cuenta.infrastructure.adapter.database.repository.AccountEntityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final AccountEntityRepository accountEntityRepository;
    private final UserStatusUseCase userStatusUseCase;

    @Override
    public Mono<AuthorizationResult> authorize(Mono<Authentication> authentication,
            @Nullable AuthorizationContext object) {
        String accountIdString = (String) object.getVariables().get("id");

        if (accountIdString == null) {
            return Mono.just(new AuthorizationDecision(false));
        }
        UUID accountId;
        try {
            accountId = UUID.fromString(accountIdString);
        } catch (IllegalArgumentException e) {
            return Mono.just(new AuthorizationDecision(false));
        }
        return authentication.flatMap(auth -> {
            String emailuser = auth.getName();

            return userStatusUseCase.isBloked(emailuser).flatMap(blocked -> {
                if (!blocked) {
                    return Mono.just(new AuthorizationDecision(false));
                }

                if (isAdmin(auth)) {
                    return Mono.just(new AuthorizationDecision(true));
                }

                return accountEntityRepository.existaByIdAndOwnerEmail(accountId, emailuser)
                        .map(AuthorizationDecision::new);
            });
        });
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

}
