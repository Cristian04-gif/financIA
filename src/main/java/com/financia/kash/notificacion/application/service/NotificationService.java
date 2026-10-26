package com.financia.kash.notificacion.application.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.financia.kash.shared.domain.Notification;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@Log4j2
public class NotificationService {

    // Mapa para guardar múltiples Sinks (emisores) por cada nombre de usuario (soporte multi-pestaña / multi-dispositivo)
    private final Map<String, Set<Sinks.Many<Notification>>> userSinks = new ConcurrentHashMap<>();

    public void sendToUser(String username, Notification notification) {
        if (username == null) {
            log.warn("Intento de enviar notificación a un usuario nulo");
            return;
        }

        Set<Sinks.Many<Notification>> sinks = userSinks.get(username);
        if (sinks != null && !sinks.isEmpty()) {
            sinks.forEach(sink -> {
                sink.emitNext(notification, (signalType, emitResult) -> {
                    if (emitResult.isFailure()) {
                        log.warn("Fallo al emitir notificación a {}: {}", username, emitResult);
                    }
                    return false;
                });
            });
            log.info("Notificación enviada a usuario: {} en {} sesión(es) activa(s)", username, sinks.size());
        } else {
            log.debug("Usuario {} no tiene sesiones WebSocket activas. Notificación no entregada en tiempo real.", username);
        }
    }

    public Flux<Notification> registerUserSession(String username, Sinks.Many<Notification> sessionSink) {
        userSinks.computeIfAbsent(username, key -> ConcurrentHashMap.newKeySet()).add(sessionSink);
        log.info("Nueva sesión registrada para usuario: {}. Total sesiones activas: {}", username, userSinks.get(username).size());
        return sessionSink.asFlux();
    }

    public void removeUserSession(String username, Sinks.Many<Notification> sessionSink) {
        userSinks.computeIfPresent(username, (key, sinks) -> {
            sinks.remove(sessionSink);
            if (sinks.isEmpty()) {
                log.info("Todas las sesiones cerradas para usuario: {}", username);
                return null;
            }
            log.info("Sesión cerrada para usuario: {}. Sesiones restantes: {}", username, sinks.size());
            return sinks;
        });
    }

    public void removeUser(String username) {
        userSinks.remove(username);
        log.info("Removidas todas las sesiones para el usuario: {}", username);
    }
}
