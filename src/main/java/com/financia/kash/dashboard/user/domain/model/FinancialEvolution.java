package com.financia.kash.dashboard.user.domain.model;

import java.util.List;

public record FinancialEvolution(String currency, List<MovementsMonth> periods) {

}
