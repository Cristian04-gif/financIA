package com.financia.kash.auth.application.port.output;

import org.springframework.security.core.userdetails.UserDetails;

import reactor.core.publisher.Mono;

public interface AuthenticationPort {
    Mono<String> authenticate(String username, String password);

    Mono<String> preAuthenticate(String username, String password);

    boolean validatePreAuthToken(String preToken, UserDetails userDetails);

    String generateFinalTokenWithoutPassword(UserDetails userDetails);

    Mono<String> getUsername(String token);
}
