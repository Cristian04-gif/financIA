package com.financia.kash.analisis.application.port.output;

import java.util.UUID;

import com.financia.kash.analisis.domain.model.objectives.ContributionData;
import com.financia.kash.analisis.domain.model.objectives.ObjectiveData;

import reactor.core.publisher.Flux;

public interface ObjectiveAnalysisPort {
    Flux<ObjectiveData> findObjectives(UUID userId);

    Flux<ContributionData> findContributions(UUID userId, UUID objectiveId);
}
