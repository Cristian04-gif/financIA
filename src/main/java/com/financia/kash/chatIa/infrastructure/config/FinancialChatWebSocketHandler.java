package com.financia.kash.chatIa.infrastructure.config;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.chatIa.domain.enums.MessageType;
import com.financia.kash.chatIa.domain.model.FinancialChatRequest;
import com.financia.kash.chatIa.domain.model.FinancialChatResponse;
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

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.getHandshakeInfo()
                .getPrincipal()
                .map(Principal::getName)
                .flatMap(email -> {

                    log.info("Conexión WebSocket de chat establecida para {}", email);
                    return userEntityRepository.findByEmail(email).flatMap(user -> {
                        UUID userId = user.getId();
                        Flux<WebSocketMessage> responses = session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .flatMap(payload -> processMessage(
                                        session,
                                        userId,
                                        payload));

                        return session.send(responses);
                    });
                }).switchIfEmpty(session.close());
    }

    private Mono<WebSocketMessage> processMessage(WebSocketSession session, UUID userId, String message) {
        try {
            FinancialChatRequest request = objectMapper.readValue(message, FinancialChatRequest.class);

            FinancialChatResponse response = new FinancialChatResponse(request.conversationId(), UUID.randomUUID(),
                    MessageType.ASSISTANT, "Mensaje recibido correctamente", LocalDateTime.now());

            String json = objectMapper.writeValueAsString(response);

            return Mono.just(session.textMessage(json));
        } catch (Exception e) {
            log.error("Error procesando mensaje WebSocket del usuario {}", userId, e);

            return Mono.just(session.textMessage("{\"error\":\"Mensaje inválido\"}"));
        }
    }

}
