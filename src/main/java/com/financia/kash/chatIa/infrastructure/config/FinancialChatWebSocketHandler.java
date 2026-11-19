package com.financia.kash.chatIa.infrastructure.config;

import java.security.Principal;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.chatIa.application.port.input.SendFinancialChatMessageUseCase;
import com.financia.kash.chatIa.domain.model.FinancialChatRequest;
import com.financia.kash.chatIa.domain.model.FinancialChatStreamResponse;
import com.financia.kash.usuario.infrastructure.adapter.database.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log4j2
public class FinancialChatWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    private final UserEntityRepository userEntityRepository;
    private final SendFinancialChatMessageUseCase sendFinancialChatMessageUseCase;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("FinancialChatWebSocketHandler ejecutado");
        return session.getHandshakeInfo().getPrincipal().map(Principal::getName).flatMap(email -> {

            log.info("Conexión WebSocket de chat establecida para {}", email);

            return userEntityRepository.findByEmail(email).flatMap(user -> {

                UUID userId = user.getId();

                Flux<WebSocketMessage> responses = session.receive().map(WebSocketMessage::getPayloadAsText)
                        .concatMap(payload -> processMessage(session, userId, payload));

                return session.send(responses);
            });
        })
                .switchIfEmpty(session.close());
    }

    private Flux<WebSocketMessage> processMessage(WebSocketSession session, UUID userId, String message) {

        try {

            FinancialChatRequest request = objectMapper.readValue(message, FinancialChatRequest.class);

            return sendFinancialChatMessageUseCase.execute(userId, request.conversationId(), request.message())
                    .map(response -> {

                        try {
                            String json = objectMapper.writeValueAsString(response);

                            return session.textMessage(json);

                        } catch (JsonProcessingException e) {

                            throw new IllegalArgumentException("No se pudo mapear la respuesta a JSON", e);
                        }
                    })
                    .onErrorResume(error -> {

                        log.error("Error procesando mensaje WebSocket del usuario {}", userId, error);

                        try {

                            String json = objectMapper.writeValueAsString(
                                    new FinancialChatStreamResponse(
                                            "ERROR",
                                            request.conversationId(),
                                            null,
                                            "No fue posible generar la respuesta.",
                                            null));

                            return Flux.just(
                                    session.textMessage(json));

                        } catch (JsonProcessingException e) {

                            return Flux.just(
                                    session.textMessage(
                                            "{\"type\":\"ERROR\",\"content\":\"Error interno\"}"));
                        }
                    });

        } catch (Exception e) {

            log.error(
                    "Error procesando mensaje WebSocket del usuario {}",
                    userId,
                    e);

            return Flux.just(
                    session.textMessage(
                            "{\"type\":\"ERROR\",\"content\":\"Mensaje inválido\"}"));
        }
    }

}
