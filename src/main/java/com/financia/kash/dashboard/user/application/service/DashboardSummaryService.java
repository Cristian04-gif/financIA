package com.financia.kash.dashboard.user.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financia.kash.cuenta.cuenta.application.port.input.GetAccountUseCase;
import com.financia.kash.cuenta.cuenta.domain.model.Account;
import com.financia.kash.dashboard.user.application.port.input.DashboardSummaryUseCase;
import com.financia.kash.dashboard.user.application.port.input.command.DashboardSummaryCommand;
import com.financia.kash.dashboard.user.application.port.output.RetrieveAllBudgetPort;
import com.financia.kash.dashboard.user.application.port.output.RetrieveAllGoalsPort;
import com.financia.kash.dashboard.user.application.port.output.RetrieveAllTransactionsPort;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary;
import com.financia.kash.dashboard.user.domain.model.MovementsMonth;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary.BudgetDashboard;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary.ComparisonPreviousPeriod;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary.DetailedFinancialSummary;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary.Goals;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary.Period;
import com.financia.kash.meta.domain.enums.ObjectiveState;
import com.financia.kash.meta.domain.model.FinancialObjectives;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.presupuesto.domain.model.Budget;
import com.financia.kash.shared.application.service.CacheService;
import com.financia.kash.shared.domain.utils.statics.CacheKeys;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DashboardSummaryService implements DashboardSummaryUseCase {

        private final GetAccountUseCase getAccountUseCase;
        private final RetrieveAllTransactionsPort retrieveAllTransactionsPort;
        private final RetrieveAllBudgetPort retrieveAllBudgetPort;
        private final RetrieveAllGoalsPort retrieveAllGoalsPort;
        private final CacheService<DashboardSummary> cacheService;

        @Override
        @Transactional(readOnly = true)
        public Mono<DashboardSummary> dashboardSummary(DashboardSummaryCommand command) {
                String cacheKey = CacheKeys.dashboardSummary(command.userId(), command.from(), command.until());

                return cacheService.retrieveMonoCache(cacheKey, DashboardSummary.class)
                                .switchIfEmpty(Mono.defer(() -> computeDashboardSummary(command))
                                                .flatMap(summary -> cacheService.saveMonoCache(cacheKey, summary, 15)));
        }

        private Mono<DashboardSummary> computeDashboardSummary(DashboardSummaryCommand command) {
                Mono<DetailedFinancialSummary> financialSummaryMono = detailedFinancial(command.userId());

                Mono<ComparisonPreviousPeriod> comparisonPreviousPeriodMono = comparisonPreviousPeriod(
                                command.userId());

                Mono<BudgetDashboard> budgetMono = budget(command.userId(), command.from().atTime(LocalTime.now()),
                                command.until().atTime(LocalTime.now()));

                Mono<Goals> goalsMono = goals(command.userId(), command.from(),
                                command.until());

                return Mono.zip(financialSummaryMono, comparisonPreviousPeriodMono, budgetMono, goalsMono)
                                .map(tuple -> {
                                        DetailedFinancialSummary detailedFinancialSummary = tuple.getT1();
                                        ComparisonPreviousPeriod comparisonPreviousPeriod = tuple.getT2();
                                        BudgetDashboard budgetDashboard = tuple.getT3();
                                        Goals goals = tuple.getT4();

                                        return new DashboardSummary(new Period(command.from(), command.until()),
                                                        detailedFinancialSummary, comparisonPreviousPeriod,
                                                        budgetDashboard, goals);

                                });
        }

        private Mono<DetailedFinancialSummary> detailedFinancial(UUID userId) {
                Flux<Account> accountFlux = getAccountUseCase.getAllMyAccount(userId);
                Mono<MovementsMonth> momvementsMonthMono = retrieveAllTransactionsPort.retriveMovementsMotnh(userId);
                return Mono.zip(accountFlux.collectList(), momvementsMonthMono).map(tuple -> {
                        BigDecimal totalCash = tuple.getT1().stream().map(Account::getCurrentBalance)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        MovementsMonth momvementsMonth = tuple.getT2();

                        BigDecimal balance = momvementsMonth.totalIncome().subtract(momvementsMonth.totalExpenses());
                        return new DetailedFinancialSummary(totalCash, momvementsMonth.totalIncome(),
                                        momvementsMonth.totalExpenses(), balance,
                                        "PEN");
                });

        }

        private Mono<ComparisonPreviousPeriod> comparisonPreviousPeriod(UUID userId) {

                return retrieveAllTransactionsPort
                                .retrievePeriodMovement(userId, 2).collectList().map(period -> {
                                        List<MovementsMonth> list = period;

                                        if (list.size() == 0 || list.size() != 2) {
                                                return new ComparisonPreviousPeriod();
                                        }
                                        MovementsMonth actual = list.getFirst();
                                        MovementsMonth anterior = list.getLast();

                                        Double actualIngreso = actual.totalIncome() != null
                                                        || !actual.totalIncome().equals(BigDecimal.ZERO)
                                                                        ? actual.totalIncome().doubleValue()
                                                                        : 0.00;
                                        Double anteriorIngreso = anterior.totalIncome() != null
                                                        || !anterior.totalExpenses().equals(BigDecimal.ZERO)
                                                                        ? anterior.totalExpenses().doubleValue()
                                                                        : 0.00;

                                        Double variacionIngresos = actualIngreso != 0.00 && anteriorIngreso != 0.00
                                                        ? ((actualIngreso - anteriorIngreso) / anteriorIngreso)
                                                                        * 100
                                                        : 0.00;

                                        System.out.println("ingreso actual: " + actualIngreso);
                                        System.out.println("anterior ingreso: " + anteriorIngreso);
                                        System.out.println(variacionIngresos);

                                        Double actualGasto = actual.totalExpenses() != null
                                                        && !actual.totalExpenses().equals(BigDecimal.ZERO)
                                                                        ? actual.totalExpenses().doubleValue()
                                                                        : 0.00;

                                        Double anteriorGasto = anterior.totalExpenses() != null
                                                        && !anterior.totalExpenses().equals(BigDecimal.ZERO)
                                                                        ? anterior.totalExpenses().doubleValue()
                                                                        : 0.00;

                                        Double variaciongastos = anteriorGasto != 0.00 & actualGasto != 0.00
                                                        ? (((actualGasto - anteriorGasto) / anteriorGasto)
                                                                        * 100)
                                                        : 0.00;

                                        return new ComparisonPreviousPeriod(variacionIngresos,
                                                        (double) Math.round(variaciongastos * 100) / 100);

                                });

        }

        private Mono<BudgetDashboard> budget(UUID userId, LocalDateTime from, LocalDateTime until) {
                Flux<Budget> budgetFlux = retrieveAllBudgetPort.findAllUserId(userId, from, until);

                Mono<BigDecimal> budgetAmountTotalMono = budgetFlux.map(Budget::amountLimitTotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                Flux<Movement> movementsFlux = budgetFlux.flatMap(budget -> {
                        return Flux.fromIterable(budget.categories()).flatMap(category -> {

                                return retrieveAllTransactionsPort
                                                .retrieveAllTransactionByCategory(userId, category.id(), from, until);
                        });
                });

                return Mono.zip(budgetAmountTotalMono, movementsFlux.collectList()).map(tuple -> {
                        BigDecimal montoTotal = tuple.getT1();
                        List<Movement> list = tuple.getT2();

                        BigDecimal montogastado = list.stream().map(Movement::getAmount).reduce(BigDecimal.ZERO,
                                        BigDecimal::add);

                        BigDecimal montoDisponible = montoTotal.subtract(montogastado);

                        Double porcenjate = (montogastado.doubleValue() / montoTotal.doubleValue()) * 100;

                        return new BudgetDashboard(montoTotal, montogastado, montoDisponible,
                                        porcenjate);
                });
        }

        private Mono<Goals> goals(UUID userId, LocalDate from, LocalDate until) {
                Flux<FinancialObjectives> goalsFlux = retrieveAllGoalsPort.retrieveAllGoals(userId, from, until);

                Flux<FinancialObjectives> activasFlux = goalsFlux
                                .filter(obj -> !obj.getStatus().equals(ObjectiveState.CANCELADA)
                                                && !obj.getStatus().equals(ObjectiveState.PAUSADA));

                Mono<Long> completas = goalsFlux.filter(obj -> obj.getStatus().equals(ObjectiveState.COMPLETADA))
                                .count();

                return Mono.zip(activasFlux.collectList(), completas).map(tuple -> {
                        List<FinancialObjectives> list = tuple.getT1();

                        int activas = (int) list.size();
                        Double porcentajeCompletado = list.stream().map(FinancialObjectives::getPercentageProgress)
                                        .reduce(0.00, Double::sum) / activas;

                        return new Goals(activas, tuple.getT2().intValue(), porcentajeCompletado);
                });
        }

}
