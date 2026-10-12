package com.financia.kash.meta.application.port.input.command;

import java.util.UUID;

import com.financia.kash.meta.domain.enums.ObjectiveState;

public record UpdateStatusCommand(UUID id, ObjectiveState objectiveState) {

}
