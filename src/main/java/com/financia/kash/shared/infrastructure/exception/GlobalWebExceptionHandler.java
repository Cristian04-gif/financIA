package com.financia.kash.shared.infrastructure.exception;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.shared.domain.exception.ErrorResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalWebExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (!(ex instanceof NoResourceFoundException)) {
            return Mono.error(ex);
        }

        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse("la ruta solicitada no existe", HttpStatus.NOT_FOUND.name());
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);

            DataBuffer buffer = response.bufferFactory().wrap(bytes);

            return response.writeWith(Mono.just(buffer));

        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

}
