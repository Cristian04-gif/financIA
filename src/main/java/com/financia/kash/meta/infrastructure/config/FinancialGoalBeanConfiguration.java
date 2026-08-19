package com.financia.kash.meta.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.financia.kash.meta.application.port.output.FinancialGoalRepositoryPort;
import com.financia.kash.meta.application.service.FinancialGoalService;

@Configuration
public class FinancialGoalBeanConfiguration {

    @Bean
    public FinancialGoalService financialGoalService(FinancialGoalRepositoryPort financialGoalRepositoryPort) {
        return new FinancialGoalService(financialGoalRepositoryPort);
    }
}
