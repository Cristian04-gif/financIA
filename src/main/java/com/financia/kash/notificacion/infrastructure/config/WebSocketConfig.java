package com.financia.kash.notificacion.infrastructure.config;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import com.financia.kash.chatIa.infrastructure.config.FinancialChatWebSocketHandler;
import com.financia.kash.notificacion.infrastructure.websocket.NotificationWebSocketHandler;

@Configuration
public class WebSocketConfig {
    @Bean
    public HandlerMapping webSocketMapping(NotificationWebSocketHandler notificationHandler,
            FinancialChatWebSocketHandler chatHandler) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();

        mapping.setUrlMap(Map.of(
                "/ws/notifications", notificationHandler,
                "/ws/financial-chat", chatHandler));

        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("http://127.0.0.1:5500"));
        cors.setAllowedMethods(List.of("GET"));
        cors.setAllowedHeaders(List.of("*"));

        mapping.setCorsConfigurations(Map.of(
                "/ws/**", cors));

        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
