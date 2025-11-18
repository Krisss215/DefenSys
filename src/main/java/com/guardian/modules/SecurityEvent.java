package com.guardian.modules;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Событие безопасности, которое агент отправляет в локальный сервис.
 */
public class SecurityEvent {

    private final Instant timestamp;
    private final SecurityEventType type;
    private final String message;
    private final Map<String, String> details;

    public SecurityEvent(SecurityEventType type, String message) {
        this(type, message, null);
    }

    public SecurityEvent(SecurityEventType type, String message, Map<String, String> details) {
        this.timestamp = Instant.now();
        this.type = type;
        this.message = message;
        if (details == null) {
            this.details = Collections.emptyMap();
        } else {
            this.details = Collections.unmodifiableMap(new HashMap<>(details));
        }
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public SecurityEventType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getDetails() {
        return details;
    }
}

