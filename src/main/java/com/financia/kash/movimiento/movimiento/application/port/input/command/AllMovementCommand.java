package com.financia.kash.movimiento.movimiento.application.port.input.command;

import java.util.UUID;

import com.financia.kash.shared.domain.PaginationRequest;

public record AllMovementCommand(UUID userId, PaginationRequest request) {

}
