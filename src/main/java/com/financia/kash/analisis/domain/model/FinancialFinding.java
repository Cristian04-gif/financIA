package com.financia.kash.analisis.domain.model;

import java.math.BigDecimal;

public record FinancialFinding(
        String code,
        FindingType type,
        FindingSeverity severity,
        String title,
        String description,
        BigDecimal value) {

}
