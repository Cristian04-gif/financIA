package com.financia.kash.usuario.infrastructure.adapter.database;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.financia.kash.auth.application.port.output.AuthenticationPort;
import com.financia.kash.usuario.application.port.output.JwtForUsersPort;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationAdapter implements AuthenticationPort {

    private final AuthenticationManager authenticationManager;
    private final JwtForUsersPort jwtForUsersPort;

    @Override
    public String authenticate(String username, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        return jwtForUsersPort.generateToken(userEntity);
    }

}
