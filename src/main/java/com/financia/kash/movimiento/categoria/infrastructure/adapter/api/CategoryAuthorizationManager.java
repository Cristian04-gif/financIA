package com.financia.kash.movimiento.categoria.infrastructure.adapter.api;

import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;

import com.financia.kash.auth.application.port.output.UserStatusUseCase;
import com.financia.kash.movimiento.categoria.infrastructure.adapter.database.repository.CategoryEntityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log4j2
public class CategoryAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final CategoryEntityRepository categoryEntityRepository;
    private final UserStatusUseCase userStatusUseCase;

    @Override
    public Mono<AuthorizationResult> authorize(Mono<Authentication> authentication,
            @Nullable AuthorizationContext object) {

        String categoryIdString = (String) object.getVariables().get("id");
        log.info("categoryId: {}", categoryIdString);
        if (categoryIdString == null) {
            return Mono.just(new AuthorizationDecision(false));
        }

        UUID categoryId;
        try {
            categoryId = UUID.fromString(categoryIdString);
        } catch (IllegalArgumentException e) {
            return Mono.just(new AuthorizationDecision(false));
        }
        return authentication.flatMap(auth -> {
            String emailuser = auth.getName();
            log.info("email. {}", emailuser);
            return userStatusUseCase.isBloked(emailuser).flatMap(blocked -> {

                if (!blocked) {
                    return Mono.just(new AuthorizationDecision(false));
                }

                if (isAdmin(auth)) {
                    return Mono.just(new AuthorizationDecision(true));
                }

                return categoryEntityRepository.existsByIdAndOwnerEmail(categoryId, emailuser)
                        .map(AuthorizationDecision::new);
            });
        });
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }
}
