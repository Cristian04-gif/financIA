package com.financia.kash.movimiento.movimiento.domain.exception;

public class FailureSendReceiptException extends RuntimeException {

    public FailureSendReceiptException() {
        super("La carga de comprobante fallo");
    }

}
