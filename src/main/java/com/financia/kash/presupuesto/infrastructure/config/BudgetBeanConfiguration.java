package com.financia.kash.presupuesto.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.financia.kash.presupuesto.application.port.output.BudgetRepositoryPort;
import com.financia.kash.presupuesto.application.service.BudgetService;

@Configuration
public class BudgetBeanConfiguration {

    @Bean
    public BudgetService budgetService(BudgetRepositoryPort budgetRepositoryPort) {
        return new BudgetService(budgetRepositoryPort);
    }
}
