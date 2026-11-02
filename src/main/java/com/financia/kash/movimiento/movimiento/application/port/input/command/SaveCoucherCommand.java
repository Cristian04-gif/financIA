package com.financia.kash.movimiento.movimiento.application.port.input.command;

import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public record SaveCoucherCommand(UUID movementId, Mono<FilePart> filePart) {

}
