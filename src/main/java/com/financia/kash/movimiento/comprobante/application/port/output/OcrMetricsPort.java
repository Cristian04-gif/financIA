package com.financia.kash.movimiento.comprobante.application.port.output;

import reactor.core.publisher.Mono;

public interface OcrMetricsPort {
    Mono<Void> record(boolean successful);
}
