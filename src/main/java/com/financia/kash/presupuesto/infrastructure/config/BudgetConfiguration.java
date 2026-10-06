package com.financia.kash.presupuesto.infrastructure.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import com.financia.kash.presupuesto.application.port.output.*;
import com.financia.kash.presupuesto.application.service.BudgetService;

@Configuration
public class BudgetConfiguration {
    @Bean
    public BudgetService budgetService(BudgetRepositoryPort repository, BudgetCategoryPort categories,
            BudgetOwnerPort owners) {
        return new BudgetService(repository, categories, owners, Clock.systemDefaultZone());
    }

    @Bean
    public TransactionalOperator budgetTransactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}

