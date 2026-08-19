package com.financia.kash.transaccion.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.financia.kash.transaccion.application.port.output.TransactionRepositoryPort;
import com.financia.kash.transaccion.application.service.TransactionService;

@Configuration
public class TransactionBeanConfiguration {

    @Bean
    public TransactionService transactionService(TransactionRepositoryPort transactionRepositoryPort) {
        return new TransactionService(transactionRepositoryPort);
    }
}
