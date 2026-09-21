package com.financia.kash.auth.infrastructure.security.service;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;

import com.financia.kash.auth.application.port.output.UserStatusUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log4j2
public class UserStatusAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final UserStatusUseCase userStatusService;

    @Override
    public Mono<AuthorizationResult> authorize(Mono<Authentication> authentication,
            @Nullable AuthorizationContext object) {
        return authentication.flatMap(auth -> {
            String emailUser = auth.getName();
            log.info("email: {}", emailUser);
            return userStatusService.isBloked(emailUser)
                    .map(blocked -> new AuthorizationDecision(!blocked));
        });
    }

}
