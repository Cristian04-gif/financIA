package com.financia.kash.auth.infrastructure.security.adapter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.financia.kash.auth.application.port.output.AuthenticationPort;
import com.financia.kash.auth.infrastructure.security.service.JwtService;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationAdapter implements AuthenticationPort {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public String authenticate(String username, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        return jwtService.generateToken(userEntity);
    }

    @Override
    public String preAuthenticate(String username, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        return jwtService.generatePreToken(userEntity);
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
    public String getUsername(String token) {
        return jwtService.getUsername(token);
    }

    @Override
    public String generateFinalTokenWithoutPassword(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }

}
