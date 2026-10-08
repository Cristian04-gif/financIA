package com.financia.kash.movimiento.comprobante.application.port.input;

import com.financia.kash.movimiento.comprobante.application.port.input.command.OrcProcessCommand;
import com.financia.kash.movimiento.comprobante.domain.model.Voucher;

import reactor.core.publisher.Mono;

public interface OcrUseCase {

    Mono<Voucher> OcrProcess(OrcProcessCommand command);
}
