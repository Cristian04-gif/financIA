package com.financia.kash.dashboard.user.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.financia.kash.dashboard.user.application.port.input.ExpensesCategoryUseCase;
import com.financia.kash.dashboard.user.application.port.input.command.ExpensesCategoryCommand;
import com.financia.kash.dashboard.user.application.port.output.RetrieveAllTransactionsPort;
import com.financia.kash.dashboard.user.domain.model.ExpensesCategory;
import com.financia.kash.dashboard.user.domain.model.ExpensesCategory.CategoriesExcenses;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExpensesCategoryService implements ExpensesCategoryUseCase {

    private final RetrieveAllTransactionsPort retrieveAllTransactionsPort;

    @Override
    public Mono<ExpensesCategory> expensesCategory(ExpensesCategoryCommand command) {
        return retrieveAllTransactionsPort.findExpensesByCategory(command.userId(), command.from(), command.until())
                .collectList().map(expenses -> {
                    BigDecimal totalExpences = expenses.stream().map(CategoriesExcenses::getAmount).reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add);

                    List<CategoriesExcenses> categoriesExcenses = expenses.stream().map(category -> {
                        Double procentage = (category.getAmount().doubleValue() / totalExpences.doubleValue()) * 100;
                        category.setPorcentage((double) Math.round(procentage * 100) / 100);
                        return category;
                    }).toList();

                    return new ExpensesCategory(totalExpences, "PEN", categoriesExcenses);
                });
    }

}
