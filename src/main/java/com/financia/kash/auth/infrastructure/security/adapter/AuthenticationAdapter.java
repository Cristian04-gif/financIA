package com.financia.kash.auth.infrastructure.security.adapter;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.financia.kash.auth.application.port.output.AuthenticationPort;
import com.financia.kash.auth.infrastructure.security.service.JwtService;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationAdapter implements AuthenticationPort {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public Mono<String> authenticate(String username, String password) {
        Authentication credentials = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(credentials).map(auth -> {
            UserEntity user = (UserEntity) auth.getPrincipal();
            return jwtService.generateToken(user);
        });
    }

    @Override
    public Mono<String> preAuthenticate(String username, String password) {
        Authentication credentials = new UsernamePasswordAuthenticationToken(
                username,
                password);

        return authenticationManager
                .authenticate(credentials)
                .map(authentication -> {

                    UserEntity user = (UserEntity) authentication.getPrincipal();

                    return jwtService.generatePreToken(user);
                });
    }

    @Override
    public boolean validatePreAuthToken(String preToken, UserDetails userDetails) {
        try {
            if (jwtService.isTokenExpired(preToken)) {
                return false;
            }
            // Convertimos tu User a UserDetails (o UserEntity si es el que implementa
            // UserDetails)

            return jwtService.isValidToken(preToken, userDetails);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Mono<String> getUsername(String token) {
        return Mono.just(jwtService.getUsername(token));
    }

    @Override
    public String generateFinalTokenWithoutPassword(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }

}
