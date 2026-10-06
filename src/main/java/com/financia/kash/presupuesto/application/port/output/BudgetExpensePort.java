package com.financia.kash.presupuesto.application.port.output;

import java.time.LocalDate;
import java.util.UUID;
import com.financia.kash.presupuesto.application.model.BudgetExpense;
import reactor.core.publisher.Flux;

public interface BudgetExpensePort {
    Flux<BudgetExpense> findExpensesByCategory(UUID userId, LocalDate start, LocalDate end);
}
