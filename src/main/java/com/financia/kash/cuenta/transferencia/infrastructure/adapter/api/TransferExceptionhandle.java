package com.financia.kash.cuenta.transferencia.infrastructure.adapter.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financia.kash.cuenta.transferencia.domain.exception.TransferNotFoundException;
import com.financia.kash.shared.domain.exception.ErrorResponse;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class TransferExceptionhandle {

    @ExceptionHandler(TransferNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> accountNotFound(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }
}
