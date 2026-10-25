package com.financia.kash.notificacion.application.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.financia.kash.shared.domain.Notification;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class NotificationService {

    // Mapa para guardar un Sink (emisor) por cada nombre de usuario
    private final Map<String, Sinks.Many<Notification>> userSinks = new ConcurrentHashMap<>();

    public void sendToUser(String username, Notification notification) {
        Sinks.Many<Notification> sink = userSinks.get(username);
        if (sink != null) {
            sink.tryEmitNext(notification);
        }
    }

    public Flux<Notification> getUserNotifications(String username) {
        return userSinks.computeIfAbsent(username, key -> Sinks.many().multicast().onBackpressureBuffer()).asFlux();
    }

    public void removeUser(String username) {
        userSinks.remove(username);
    }
}
