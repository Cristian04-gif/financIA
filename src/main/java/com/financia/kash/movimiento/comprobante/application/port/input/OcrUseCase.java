package com.financia.kash.movimiento.comprobante.application.port.input;

import java.util.UUID;

import org.springframework.util.MimeType;

import com.financia.kash.movimiento.comprobante.domain.model.Voucher;

import reactor.core.publisher.Mono;

public interface OcrUseCase {

    Mono<Voucher> OcrProcess(UUID userId, byte[] imageByte, MimeType mimeType);
}
