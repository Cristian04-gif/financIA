package com.financia.kash.analisis.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.input.AnalyzeFinancialFindingsUseCase;
import com.financia.kash.analisis.domain.model.FinancialFinding;
import com.financia.kash.analisis.domain.model.FinancialFindings;
import com.financia.kash.analisis.domain.model.FinancialSummary;
import com.financia.kash.analisis.domain.model.FindingSeverity;
import com.financia.kash.analisis.domain.model.FindingType;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicators;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnalyzeFinancialFindingsService implements AnalyzeFinancialFindingsUseCase {
        private static final BigDecimal LOW_SAVINGS_THRESHOLD = new BigDecimal("10");

        private static final BigDecimal HEALTHY_SAVINGS_THRESHOLD = new BigDecimal("20");

        @Override
        public Mono<FinancialFindings> analyze(FinancialSummary summary) {

                if (summary == null) {
                        return Mono.error(
                                        new IllegalArgumentException(
                                                        "El resumen financiero es obligatorio"));
                }

                List<FinancialFinding> findings = new ArrayList<>();

                analyzeBalance(summary, findings);
                analyzeSavingsRate(summary, findings);
                analyzeObjectives(summary.objectives(), findings);

                return Mono.just(new FinancialFindings(
                                summary.startDate(),
                                summary.endDate(),
                                findings.size(),
                                List.copyOf(findings)));
        }

        private void analyzeBalance(
                        FinancialSummary summary,
                        List<FinancialFinding> findings) {

                BigDecimal balance = summary.financialIndicators()
                                .operatingBalance();

                if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        findings.add(new FinancialFinding(
                                        "DEFICIT_OPERATIVO",
                                        FindingType.FINANCIAL,
                                        FindingSeverity.CRITICAL,
                                        "Déficit operativo",
                                        "Los egresos superaron los ingresos del período.",
                                        balance));

                } else if (balance.compareTo(BigDecimal.ZERO) > 0) {
                        findings.add(new FinancialFinding(
                                        "BALANCE_POSITIVO",
                                        FindingType.FINANCIAL,
                                        FindingSeverity.SUCCESS,
                                        "Balance operativo positivo",
                                        "Los ingresos superaron los egresos del período.",
                                        balance));
                }
        }

        private void analyzeSavingsRate(
                        FinancialSummary summary,
                        List<FinancialFinding> findings) {

                BigDecimal income = summary.financialIndicators().income();

                if (income.compareTo(BigDecimal.ZERO) <= 0) {
                        return;
                }

                BigDecimal rate = summary.savingsRate();

                if (rate.compareTo(LOW_SAVINGS_THRESHOLD) < 0) {
                        findings.add(new FinancialFinding(
                                        "AHORRO_BAJO",
                                        FindingType.FINANCIAL,
                                        FindingSeverity.WARNING,
                                        "Tasa de ahorro baja",
                                        "El porcentaje de ingresos destinado al ahorro es inferior al 10%.",
                                        rate));

                } else if (rate.compareTo(HEALTHY_SAVINGS_THRESHOLD) >= 0) {
                        findings.add(new FinancialFinding(
                                        "AHORRO_SALUDABLE",
                                        FindingType.FINANCIAL,
                                        FindingSeverity.SUCCESS,
                                        "Buena tasa de ahorro",
                                        "El usuario está destinando al menos el 20% de sus ingresos al ahorro.",
                                        rate));
                }
        }

        private void analyzeObjectives(
                        ObjectiveIndicators objectives,
                        List<FinancialFinding> findings) {

                objectives.objectives().forEach(objective -> {

                        switch (objective.progressStatus()) {

                                case VENCIDA -> findings.add(new FinancialFinding(
                                                "META_VENCIDA",
                                                FindingType.OBJECTIVE,
                                                FindingSeverity.CRITICAL,
                                                "Meta vencida",
                                                "La meta " + objective.objectiveName()
                                                                + " superó su fecha objetivo.",
                                                objective.remainingAmount()));

                                case RITMO_INSUFICIENTE -> findings.add(new FinancialFinding(
                                                "META_RITMO_INSUFICIENTE",
                                                FindingType.OBJECTIVE,
                                                FindingSeverity.WARNING,
                                                "Meta en riesgo",
                                                "El ritmo de aportes de "
                                                                + objective.objectiveName()
                                                                + " es insuficiente para alcanzar el objetivo.",
                                                objective.monthlyDifference()));

                                case EN_RITMO -> findings.add(new FinancialFinding(
                                                "META_EN_RITMO",
                                                FindingType.OBJECTIVE,
                                                FindingSeverity.SUCCESS,
                                                "Meta en buen ritmo",
                                                "La meta " + objective.objectiveName()
                                                                + " mantiene un ritmo de ahorro suficiente.",
                                                objective.monthlyDifference()));

                                case COMPLETADA -> findings.add(new FinancialFinding(
                                                "META_COMPLETADA",
                                                FindingType.OBJECTIVE,
                                                FindingSeverity.SUCCESS,
                                                "Meta completada",
                                                "Se alcanzó el objetivo de "
                                                                + objective.objectiveName() + ".",
                                                objective.targetAmount()));
                        }
                });
        }
}
