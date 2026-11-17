package com.financia.kash.analisis.domain.model;

import java.time.LocalDate;
import java.util.List;

public record FinancialFindings(
        LocalDate startDate,
        LocalDate endDate,
        int totalFindings,
        List<FinancialFinding> findings) {

}
