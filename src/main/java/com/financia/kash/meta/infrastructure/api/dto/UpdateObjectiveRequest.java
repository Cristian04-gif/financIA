package com.financia.kash.meta.infrastructure.api.dto;

import com.financia.kash.meta.domain.enums.ObjectiveState;

import jakarta.validation.constraints.NotNull;

public record UpdateObjectiveRequest(@NotNull ObjectiveState objectiveState) {

}
