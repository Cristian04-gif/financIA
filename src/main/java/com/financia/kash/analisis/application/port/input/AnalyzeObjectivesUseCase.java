package com.financia.kash.analisis.application.port.input;

import java.util.UUID;

import com.financia.kash.analisis.domain.model.objectives.ObjectiveIndicators;

import reactor.core.publisher.Mono;

public interface AnalyzeObjectivesUseCase {
    Mono<ObjectiveIndicators> analyze(UUID userId);
}
