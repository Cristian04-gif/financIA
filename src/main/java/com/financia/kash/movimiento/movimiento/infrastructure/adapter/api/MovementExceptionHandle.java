package com.financia.kash.movimiento.movimiento.infrastructure.adapter.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financia.kash.movimiento.movimiento.domain.exception.FailureSendReceiptException;
import com.financia.kash.movimiento.movimiento.domain.exception.InvalidMovementTypeException;
import com.financia.kash.movimiento.movimiento.domain.exception.MovementNotFoundException;
import com.financia.kash.shared.domain.exception.ErrorResponse;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class MovementExceptionHandle {

    @ExceptionHandler(MovementNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> movementNotFount(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    @ExceptionHandler(InvalidMovementTypeException.class)
    public Mono<ResponseEntity<ErrorResponse>> invalidMovementType(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(FailureSendReceiptException.class)
    public Mono<ResponseEntity<ErrorResponse>> failureSendReceipt(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

}
