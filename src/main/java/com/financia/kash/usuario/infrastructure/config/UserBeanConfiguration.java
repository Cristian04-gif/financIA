package com.financia.kash.usuario.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.financia.kash.usuario.application.port.output.PasswordEncoderForUserPort;
import com.financia.kash.usuario.application.port.output.UserRepositoryPort;
import com.financia.kash.usuario.application.service.UserService;

@Configuration
public class UserBeanConfiguration {

    @Bean
    public UserService userService(UserRepositoryPort userRepositoryPort,
            PasswordEncoderForUserPort passwordEncoderForUserPort) {
        return new UserService(userRepositoryPort, passwordEncoderForUserPort);
    }
}
