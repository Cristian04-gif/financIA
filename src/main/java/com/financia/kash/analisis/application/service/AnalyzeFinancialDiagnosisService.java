package com.financia.kash.analisis.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.financia.kash.analisis.application.port.input.AnalyzeFinancialDiagnosisUseCase;
import com.financia.kash.analisis.domain.model.FinancialSummary;
import com.financia.kash.analisis.domain.model.diagnosis.DiagnosisSeverity;
import com.financia.kash.analisis.domain.model.diagnosis.DiagnosisType;
import com.financia.kash.analisis.domain.model.diagnosis.FinancialDiagnosis;
import com.financia.kash.analisis.domain.model.diagnosis.FinancialDiagnosisItem;
import com.financia.kash.analisis.domain.model.finging.FinancialFindings;
import com.financia.kash.analisis.domain.model.period.FinancialPeriodComparison;
import com.financia.kash.analisis.domain.model.period.MetricComparison;

import reactor.core.publisher.Mono;

@Service
public class AnalyzeFinancialDiagnosisService implements AnalyzeFinancialDiagnosisUseCase {
    @Override
    public Mono<FinancialDiagnosis> analyze(
            FinancialSummary summary,
            FinancialFindings findings) {

        if (summary == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "El resumen financiero es obligatorio"));
        }

        if (findings == null) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Los hallazgos financieros son obligatorios"));
        }

        List<FinancialDiagnosisItem> diagnoses = new ArrayList<>();

        analyzeSavingsWithDeficit(summary, findings, diagnoses);
        analyzeNegativeAvailability(summary, diagnoses);
        analyzeObjectives(findings, diagnoses);
        analyzeExpenseGrowth(summary, diagnoses);

        return Mono.just(
                new FinancialDiagnosis(
                        summary.startDate(),
                        summary.endDate(),
                        diagnoses.size(),
                        List.copyOf(diagnoses)));
    }

    private void analyzeSavingsWithDeficit(
            FinancialSummary summary,
            FinancialFindings findings,
            List<FinancialDiagnosisItem> diagnoses) {

        boolean hasDeficit = findings.findings().stream()
                .anyMatch(finding -> finding.code().equals("DEFICIT_OPERATIVO"));

        boolean hasHealthySavings = findings.findings().stream()
                .anyMatch(finding -> finding.code().equals("AHORRO_SALUDABLE"));

        if (hasDeficit && hasHealthySavings) {
            diagnoses.add(new FinancialDiagnosisItem(
                    "AHORRO_CON_DEFICIT",
                    DiagnosisType.SAVINGS,
                    DiagnosisSeverity.WARNING,
                    "Ahorro elevado con déficit operativo",
                    "El usuario mantiene una tasa de ahorro elevada, pero los egresos superaron los ingresos durante el período. El nivel de ahorro registrado debe analizarse junto con la disponibilidad de fondos."));
        }
    }

    private void analyzeNegativeAvailability(
            FinancialSummary summary,
            List<FinancialDiagnosisItem> diagnoses) {

        BigDecimal unallocatedFunds = summary.financialIndicators()
                .unallocatedFunds();

        if (unallocatedFunds.compareTo(BigDecimal.ZERO) < 0) {
            diagnoses.add(new FinancialDiagnosisItem(
                    "DISPONIBILIDAD_NEGATIVA",
                    DiagnosisType.CASH_FLOW,
                    DiagnosisSeverity.CRITICAL,
                    "Disponibilidad financiera negativa",
                    "Los fondos no asignados del período son negativos, indicando que las salidas y asignaciones registradas superan los recursos disponibles."));
        }
    }

    private void analyzeObjectives(
            FinancialFindings findings,
            List<FinancialDiagnosisItem> diagnoses) {

        boolean hasObjectivesAtRisk = findings.findings().stream()
                .anyMatch(finding -> finding.code().equals("META_RITMO_INSUFICIENTE"));

        if (hasObjectivesAtRisk) {
            diagnoses.add(new FinancialDiagnosisItem(
                    "META_EN_RIESGO",
                    DiagnosisType.OBJECTIVE,
                    DiagnosisSeverity.WARNING,
                    "Existe una meta en riesgo",
                    "Al menos una meta financiera no mantiene el ritmo de aportes necesario para alcanzar el objetivo dentro del plazo establecido."));
        }
    }

    private void analyzeExpenseGrowth(
            FinancialSummary summary,
            List<FinancialDiagnosisItem> diagnoses) {

        FinancialPeriodComparison comparison = summary.periodComparison();

        MetricComparison income = comparison.income();
        MetricComparison expenses = comparison.expenses();

        // No podemos comparar porcentajes si no existe una base anterior.
        if (!income.hasPreviusBase() || !expenses.hasPreviusBase()) {
            return;
        }

        // Solo nos interesa cuando los gastos aumentan.
        if (expenses.porcentageChange().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // Los gastos crecen más rápido que los ingresos.
        if (expenses.porcentageChange()
                .compareTo(income.porcentageChange()) > 0) {

            diagnoses.add(new FinancialDiagnosisItem(
                    "PRESION_GASTOS",
                    DiagnosisType.SPENDING,
                    DiagnosisSeverity.WARNING,
                    "Los gastos crecen más rápido que los ingresos",
                    "Los gastos han aumentado proporcionalmente más que los ingresos respecto al período anterior. Esta tendencia podría reducir la capacidad de ahorro y el dinero disponible."));
        }
    }
}
