package com.financia.kash.analisis.domain.model.period;

import java.math.BigDecimal;

public record MetricComparison(
                BigDecimal currentValue,
                BigDecimal previusValue,
                BigDecimal absoluteDifference,
                BigDecimal porcentageChange,
                boolean hasPreviusBase) {

}
