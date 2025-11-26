// src/main/java/com/guardian/logging/SecureLogEntry.java
package com.guardian.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SecureLogEntry {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final LocalDateTime timestamp;
    private final String eventType;
    private final String source;
    private final String message;

    public SecureLogEntry(String eventType, String source, String message) {
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
        this.source = source;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSource() {
        return source;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedTime() {
        return TIME_FORMATTER.format(timestamp);
    }

    @Override
    public String toString() {
        return getFormattedTime() + " [" + eventType + "] " + message;
    }
}
