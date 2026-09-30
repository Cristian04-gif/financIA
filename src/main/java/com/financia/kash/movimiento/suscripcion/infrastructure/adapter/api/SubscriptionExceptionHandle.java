package com.financia.kash.movimiento.suscripcion.infrastructure.adapter.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financia.kash.movimiento.suscripcion.domain.exception.FrequencyNotFoundException;
import com.financia.kash.movimiento.suscripcion.domain.exception.SubscriptionNotfoundException;
import com.financia.kash.shared.domain.exception.ErrorResponse;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class SubscriptionExceptionHandle {

    @ExceptionHandler(SubscriptionNotfoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> userNotFound(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    @ExceptionHandler(FrequencyNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> frequencyNotFound(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

}
