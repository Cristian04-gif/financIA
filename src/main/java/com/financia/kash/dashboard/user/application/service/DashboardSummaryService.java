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
import com.financia.kash.dashboard.user.domain.model.DashboardSummary.BudgetDashboard;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary.ComparisonPreviousPeriod;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary.DetailedFinancialSummary;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary.Goals;
import com.financia.kash.dashboard.user.domain.model.DashboardSummary.Period;
import com.financia.kash.meta.domain.enums.ObjectiveState;
import com.financia.kash.meta.domain.model.FinancialObjectives;
import com.financia.kash.movimiento.movimiento.domain.enums.TypeMovement;
import com.financia.kash.movimiento.movimiento.domain.model.Movement;
import com.financia.kash.presupuesto.domain.model.Budget;

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

        @Override
        @Transactional(readOnly = true)
        public Mono<DashboardSummary> dashboardSummary(DashboardSummaryCommand command) {
                Mono<DetailedFinancialSummary> financialSummaryMono = detailedFinancial(command.userId(),
                                command.from().atTime(LocalTime.now()),
                                command.until().atTime(LocalTime.now()));

                Mono<ComparisonPreviousPeriod> comparisonPreviousPeriodMono = comparisonPreviousPeriod(
                                command.userId(), command.from().atTime(LocalTime.now()),
                                command.until().atTime(LocalTime.now()));

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

        private Mono<DetailedFinancialSummary> detailedFinancial(UUID userId, LocalDateTime from, LocalDateTime until) {

                Flux<Movement> movementFlux = retrieveAllTransactionsPort
                                .retrieveAllTransactions(userId, from, until);

                Mono<BigDecimal> totalAmountMono = getAccountUseCase.getAllMyAccount(userId)
                                .map(Account::getCurrentBalance)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                Mono<BigDecimal> movementsEgressMono = movementFlux
                                .filter(mov -> mov.getType().equals(TypeMovement.EGRESO))
                                .map(Movement::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                Mono<BigDecimal> movementsIncomeMono = movementFlux
                                .filter(mov -> mov.getType().equals(TypeMovement.INGRESO))
                                .map(Movement::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return Mono.zip(totalAmountMono, movementsEgressMono, movementsIncomeMono, movementFlux.collectList())
                                .map(tuple -> {

                                        BigDecimal total = tuple.getT1();
                                        BigDecimal movementIncome = tuple.getT3();
                                        BigDecimal movementsEgress = tuple.getT2();

                                        BigDecimal periodBalance = total.subtract(movementsEgress);

                                        return new DetailedFinancialSummary(total, movementIncome, movementsEgress,
                                                        periodBalance, "PEN");
                                });
        }

        private Mono<ComparisonPreviousPeriod> comparisonPreviousPeriod(UUID userId, LocalDateTime from,
                        LocalDateTime until) {
                LocalDateTime today = LocalDateTime.now();

                LocalDateTime startDate = today.minusMonths(1).withDayOfMonth(1);

                LocalDateTime endDate = today.withDayOfMonth(1).minusDays(1);

                Flux<Movement> movementPeridoActualFlux = retrieveAllTransactionsPort
                                .retrieveAllTransactions(userId, from, until);

                Flux<Movement> movementPeridoAnteriorFlux = retrieveAllTransactionsPort
                                .retrieveAllTransactions(userId, startDate,
                                                endDate);

                return Mono.zip(movementPeridoActualFlux.collectList(), movementPeridoAnteriorFlux.collectList())
                                .map(tuple -> {
                                        if (tuple.getT2().size() == 0) {
                                                return new ComparisonPreviousPeriod();
                                        }
                                        BigDecimal ingresosActuales = tuple.getT1().stream()
                                                        .filter(mov -> mov.getType().equals(TypeMovement.INGRESO))
                                                        .map(Movement::getAmount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        BigDecimal gastosActuales = tuple.getT1().stream()
                                                        .filter(mov -> mov.getType().equals(TypeMovement.EGRESO))
                                                        .map(Movement::getAmount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        // anterior
                                        BigDecimal ingresosAnterior = tuple.getT2().stream()
                                                        .filter(mov -> mov.getType().equals(TypeMovement.INGRESO))
                                                        .map(Movement::getAmount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        BigDecimal gastosAnterior = tuple.getT2().stream()
                                                        .filter(mov -> mov.getType().equals(TypeMovement.EGRESO))
                                                        .map(Movement::getAmount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        // calculo

                                        Double variacionIngresos = ((ingresosActuales.doubleValue()
                                                        - ingresosAnterior.doubleValue())
                                                        / ingresosAnterior.doubleValue()) * 100;

                                        Double variacionGastos = ((gastosActuales.doubleValue()
                                                        - gastosAnterior.doubleValue()) / gastosAnterior.doubleValue())
                                                        * 100;

                                        return new ComparisonPreviousPeriod(variacionIngresos, variacionGastos);
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
