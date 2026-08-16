package com.financia.kash.usuario.infrastructure.adapter.database;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.financia.kash.auth.application.port.output.PasswordEncoderPort;
import com.financia.kash.usuario.application.port.output.PasswordEncoderForUserPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordEncoderPort, PasswordEncoderForUserPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String ecoderPassword(String password) {
        return passwordEncoder.encode(password);
    }

}
