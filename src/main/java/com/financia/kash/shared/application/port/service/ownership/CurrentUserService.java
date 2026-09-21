package com.financia.kash.shared.application.port.service.ownership;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class CurrentUserService {

    public Mono<Authentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication);
    }

    public Mono<String> getUserEmail() {
        return getAuthentication().map(Authentication::getName);
    }
}
