package com.financia.kash.shared.infrastructure.exception;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.shared.domain.exception.ErrorResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse(denied.getMessage(), HttpStatus.UNAUTHORIZED.name());

        return response.writeWith(Mono.fromCallable(() -> {
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);

                DataBuffer buffer = response.bufferFactory().wrap(bytes);

                return buffer;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }));
    }

}
