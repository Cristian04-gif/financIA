package com.financia.kash.notificacion.infrastructure.websocket;

import java.security.Principal;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financia.kash.notificacion.application.service.NotificationService;
import com.financia.kash.shared.domain.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationWebSocketHandler implements WebSocketHandler {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.getHandshakeInfo().getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> {
                    Sinks.Many<Notification> sessionSink = Sinks.many().multicast().onBackpressureBuffer();

                    Mono<Void> send = session
                            .send(notificationService.registerUserSession(username, sessionSink).map(notification -> {
                                try {
                                    String json = objectMapper.writeValueAsString(notification);
                                    return session.textMessage(json);
                                } catch (Exception e) {
                                    log.error("Error procesando notificación a JSON para {}: {}", username, e.getMessage());
                                    return session.textMessage("{\"error\": \"Error procesando notificación\"}");
                                }
                            }));

                    Mono<Void> receive = session.receive().then();

                    return Mono.zip(send, receive)
                            .then()
                            .doFinally(signalType -> notificationService.removeUserSession(username, sessionSink));
                })
                .switchIfEmpty(session.close()); // Cerrar si no hay usuario autenticado
    }

}
