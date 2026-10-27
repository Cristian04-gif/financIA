package com.financia.kash.meta.infrastructure.api.objective;

import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;

import com.financia.kash.meta.infrastructure.database.repository.ObjectiveRepository;
import com.financia.kash.shared.application.port.output.UserStatusUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ObjectiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final ObjectiveRepository objectiveRepository;
    private final UserStatusUseCase userStatusUseCase;

    @Override
    public Mono<AuthorizationResult> authorize(Mono<Authentication> authentication,
            @Nullable AuthorizationContext object) {
        String objectiveIdString = (String) object.getVariables().get("id");

        if (objectiveIdString == null) {
            return Mono.just(new AuthorizationDecision(false));
        }

        UUID objectiveId;
        try {
            objectiveId = UUID.fromString(objectiveIdString);
        } catch (IllegalArgumentException e) {
            return Mono.just(new AuthorizationDecision(false));
        }

        return authentication.flatMap(auth -> {
            String emailUser = auth.getName();

            return userStatusUseCase.isBloked(emailUser).flatMap(blocked -> {
                if (!blocked) {
                    return Mono.just(new AuthorizationDecision(false));
                }
                return objectiveRepository.existsByIdAndOwnerEmail(objectiveId, emailUser)
                        .map(AuthorizationDecision::new);
            });
        });
    }


}
