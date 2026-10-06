package com.financia.kash.presupuesto.infrastructure.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.reactive.TransactionalOperator;
import com.financia.kash.presupuesto.application.port.output.*;
import com.financia.kash.presupuesto.application.service.BudgetService;
import com.financia.kash.presupuesto.application.service.BudgetConsumptionService;
import com.financia.kash.presupuesto.application.port.input.GetBudgetUseCase;

@Configuration
public class BudgetConfiguration {
    @Bean
    public BudgetConsumptionService budgetConsumptionService(GetBudgetUseCase budgets, BudgetExpensePort expenses) {
        return new BudgetConsumptionService(budgets, expenses);
    }

    @Bean
    public BudgetService budgetService(BudgetRepositoryPort repository, BudgetCategoryPort categories,
            BudgetOwnerPort owners) {
        return new BudgetService(repository, categories, owners, Clock.systemDefaultZone());
    }

    @Bean
    public TransactionalOperator budgetTransactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }

    @Bean
    public TransactionalOperator budgetReadTransactionalOperator(ReactiveTransactionManager transactionManager) {
        var definition = new DefaultTransactionDefinition();
        definition.setReadOnly(true);
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        return TransactionalOperator.create(transactionManager, definition);
    }
}
