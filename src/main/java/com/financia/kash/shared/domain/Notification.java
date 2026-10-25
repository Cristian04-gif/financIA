package com.financia.kash.shared.domain;

import java.time.Instant;
import java.util.Map;

public record Notification(String type, String header, String body, Instant timestamp,
        Map<String, Object> data) {

    public static Notification of(String type, String header, String body,
            Map<String, Object> data) {
        return new Notification(type, header, body, Instant.now(), data);
    }
}
