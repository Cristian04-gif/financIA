package com.financia.kash.cuenta.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.financia.kash.cuenta.application.port.output.AccountRepositoryPort;
import com.financia.kash.cuenta.application.service.AccountService;

@Configuration
public class AccountBeanConfiguration {

    @Bean
    public AccountService accountService(AccountRepositoryPort accountRepositoryPort) {
        return new AccountService(accountRepositoryPort);
    }
}
