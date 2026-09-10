package com.financia.kash.auth.infrastructure.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final UserEntityRepository entityRepository;

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> entityRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("usuario no entontrado")))
                .cast(UserDetails.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(
                userDetailsService);

        manager.setPasswordEncoder(passwordEncoder);

        return manager;
    }
}
