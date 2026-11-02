package com.financia.kash.dashboard.user.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovementsMonth(LocalDate period, BigDecimal totalIncome,
        BigDecimal totalExpenses) {

}
