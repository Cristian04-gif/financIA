package com.financia.kash.meta.infrastructure.api.contribution;

import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;

import com.financia.kash.meta.infrastructure.database.repository.ContributionObjectiveRepository;
import com.financia.kash.shared.application.port.output.UserStatusUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ContributionObjectiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final ContributionObjectiveRepository contributionObjectiveRepository;
    private final UserStatusUseCase userStatusUseCase;

    @Override
    public Mono<AuthorizationResult> authorize(Mono<Authentication> authentication,
            @Nullable AuthorizationContext object) {
        String contirbutionString = (String) object.getVariables().get("id");

        if (contirbutionString == null) {
            return Mono.just(new AuthorizationDecision(false));
        }

        UUID contributionId;
        try {
            contributionId = UUID.fromString(contirbutionString);
        } catch (Exception e) {
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
                return contributionObjectiveRepository.existsByIdAndOwnerEmail(contributionId, emailUser)
                        .map(AuthorizationDecision::new);
            });
        });
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

}
