package com.financia.kash.movimiento.comprobante.application.port.input;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public interface SaveFileVoucherUseCase {

    Mono<String> saveFileVoucher(FilePart file);
}
