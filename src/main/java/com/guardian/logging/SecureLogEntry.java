package com.guardian.logging;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecureLogEntry {

    private final long index;
    private final long timestampMillis;
    private final String eventType;
    private final String severity;
    private final String message;
    private final String prevHash;
    private final String hash;

    public static final String GENESIS_HASH = "GENESIS";

    public SecureLogEntry(long index,
                          long timestampMillis,
                          String eventType,
                          String severity,
                          String message,
                          String prevHash,
                          String hash) {
        this.index = index;
        this.timestampMillis = timestampMillis;
        this.eventType = eventType;
        this.severity = severity;
        this.message = message;
        this.prevHash = prevHash;
        this.hash = hash;
    }

    public long getIndex() {
        return index;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public String getHash() {
        return hash;
    }

    public static SecureLogEntry create(long index,
                                        long timestampMillis,
                                        String eventType,
                                        String severity,
                                        String message,
                                        String prevHash) {
        String msgBase64 = Base64.getEncoder()
                .encodeToString(message.getBytes(StandardCharsets.UTF_8));

        String payload = buildPayload(index, timestampMillis, eventType, severity, msgBase64, prevHash);
        String hash = computeHash(payload);

        return new SecureLogEntry(index, timestampMillis, eventType, severity, message, prevHash, hash);
    }

    public static SecureLogEntry fromLine(String line) {
        String[] parts = line.split("\\|", 7);
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid log line format: " + line);
        }

        long index = Long.parseLong(parts[0]);
        long ts = Long.parseLong(parts[1]);
        String eventType = parts[2];
        String severity = parts[3];
        String msgBase64 = parts[4];
        String prevHash = parts[5];
        String hash = parts[6];

        String message = new String(Base64.getDecoder().decode(msgBase64), StandardCharsets.UTF_8);

        return new SecureLogEntry(index, ts, eventType, severity, message, prevHash, hash);
    }

    public String toLine() {
        String msgBase64 = Base64.getEncoder()
                .encodeToString(message.getBytes(StandardCharsets.UTF_8));
        String payload = buildPayload(index, timestampMillis, eventType, severity, msgBase64, prevHash);

        if (!hash.equals(computeHash(payload))) {
            throw new IllegalStateException("Hash mismatch for entry index " + index);
        }

        return payload + "|" + hash;
    }

    public static String recomputeHash(long index,
                                       long timestampMillis,
                                       String eventType,
                                       String severity,
                                       String message,
                                       String prevHash) {
        String msgBase64 = Base64.getEncoder()
                .encodeToString(message.getBytes(StandardCharsets.UTF_8));
        String payload = buildPayload(index, timestampMillis, eventType, severity, msgBase64, prevHash);
        return computeHash(payload);
    }

    private static String buildPayload(long index,
                                       long timestampMillis,
                                       String eventType,
                                       String severity,
                                       String msgBase64,
                                       String prevHash) {
        return index + "|" + timestampMillis + "|" + eventType + "|" + severity + "|" + msgBase64 + "|" + prevHash;
    }

    private static String computeHash(String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
