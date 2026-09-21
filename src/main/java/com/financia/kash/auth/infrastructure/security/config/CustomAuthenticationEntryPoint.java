package com.financia.kash.auth.infrastructure.security.config;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.shared.domain.exception.ErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log4j2
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        log.error("ENTRO AL AUTHENTICATIO ENTRY PONT {}", exchange.getResponse());
        ServerHttpResponse httpResponse = exchange.getResponse();

        httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        httpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.name());

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);

            DataBuffer buffer = httpResponse.bufferFactory().wrap(bytes);

            return httpResponse.writeWith(
                    Mono.just(buffer));

        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

}
