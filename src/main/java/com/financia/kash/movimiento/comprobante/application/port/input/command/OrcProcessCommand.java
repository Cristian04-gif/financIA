package com.financia.kash.movimiento.comprobante.application.port.input.command;

import java.util.UUID;

import org.springframework.util.MimeType;

public record OrcProcessCommand(UUID userId, byte[] imageByte, MimeType mimeType) {

}
