package com.financia.kash.analisis.domain.model.objectives;

import java.util.List;

public record ObjectiveIndicators(
        int totalObjectives,
        int completedObjectives,
        int activeObjectives,
        int overdueObjectives,
        List<ObjectiveIndicator> objectives) {

}
