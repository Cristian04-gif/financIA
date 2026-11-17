package com.financia.kash.analisis.domain.model.finging;

import java.math.BigDecimal;

public record FinancialFinding(
                String code,
                FindingType type,
                FindingSeverity severity,
                String title,
                String description,
                BigDecimal value) {

}
