package com.financia.kash.analisis.domain.model.savingsRate;

import java.util.List;

public record SavingsRateIndicators(
        String currency,
        List<SavingsRatePeriod> periods) {

}
