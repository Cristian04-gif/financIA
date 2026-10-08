package com.financia.kash.cuenta.transferencia.application.port.input.command;

import java.util.UUID;

import com.financia.kash.shared.domain.PaginationRequest;

public record GetTransferCommand(UUID userId, PaginationRequest request) {

}
