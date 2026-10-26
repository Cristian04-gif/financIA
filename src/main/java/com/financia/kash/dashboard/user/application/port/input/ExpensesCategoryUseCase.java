package com.financia.kash.dashboard.user.application.port.input;

import com.financia.kash.dashboard.user.application.port.input.command.ExpensesCategoryCommand;
import com.financia.kash.dashboard.user.domain.model.ExpensesCategory;

import reactor.core.publisher.Mono;

public interface ExpensesCategoryUseCase {
    Mono<ExpensesCategory> expensesCategory(ExpensesCategoryCommand command);
}
