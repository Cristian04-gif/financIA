package com.financia.kash.notificacion.infrastructure.websocket;

import java.security.Principal;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.notificacion.application.service.NotificationService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler implements WebSocketHandler {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.getHandshakeInfo().getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> {
                    Mono<Void> send = session
                            .send(notificationService.getUserNotifications(username).map(notification -> {
                                try {
                                    String json = objectMapper.writeValueAsString(notification);
                                    return session.textMessage(json);
                                } catch (Exception e) {
                                    // Manejo de error si falla la conversión
                                    return session.textMessage("{\"error\": \"Error procesando notificación\"}");
                                }
                            }));

                    Mono<Void> recieve = session.receive()
                            .then().doFinally(signalType -> notificationService.removeUser(username));

                    return Mono.zip(send, recieve).then();
                })
                .switchIfEmpty(session.close()); // Cerrar si no hay usuario autenticado
    }

}
