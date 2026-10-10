package com.financia.kash.meta.infrastructure.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financia.kash.meta.domain.exception.ContributionNotFoundException;
import com.financia.kash.meta.domain.exception.InvalidContributionAmountException;
import com.financia.kash.meta.domain.exception.InvalidStateChangeException;
import com.financia.kash.meta.domain.exception.ObjectiveNotFountException;
import com.financia.kash.meta.domain.exception.UnauthorizedContributionException;
import com.financia.kash.shared.domain.exception.ErrorResponse;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ObjectivesExeptionHandle {

    @ExceptionHandler(ObjectiveNotFountException.class)
    public Mono<ResponseEntity<ErrorResponse>> objectiveNotFountException(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    @ExceptionHandler(ContributionNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> contributionNotFoundException(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    @ExceptionHandler(InvalidContributionAmountException.class)
    public Mono<ResponseEntity<ErrorResponse>> invalidContributionAmountException(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(InvalidStateChangeException.class)
    public Mono<ResponseEntity<ErrorResponse>> invalidStateChangeException(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(UnauthorizedContributionException.class)
    public Mono<ResponseEntity<ErrorResponse>> unauthorizedContributionException(Exception exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }
}
