package com.guardian.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SecureLogManager {

    private final Path logFile;
    private long lastIndex;
    private String lastHash;

    public SecureLogManager(Path logFile) throws IOException {
        this.logFile = logFile;
        if (logFile.getParent() != null) {
            Files.createDirectories(logFile.getParent());
        }
        initState();
    }

    private void initState() throws IOException {
        if (!Files.exists(logFile)) {
            this.lastIndex = 0L;
            this.lastHash = SecureLogEntry.GENESIS_HASH;
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(logFile, StandardCharsets.UTF_8)) {
            String line;
            String lastNonEmpty = null;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lastNonEmpty = line;
                }
            }
            if (lastNonEmpty == null) {
                this.lastIndex = 0L;
                this.lastHash = SecureLogEntry.GENESIS_HASH;
            } else {
                SecureLogEntry lastEntry = SecureLogEntry.fromLine(lastNonEmpty);
                this.lastIndex = lastEntry.getIndex();
                this.lastHash = lastEntry.getHash();
            }
        }
    }

    public synchronized SecureLogEntry logEvent(String eventType,
                                                String severity,
                                                String message) throws IOException {
        long newIndex = lastIndex + 1;
        long ts = System.currentTimeMillis();

        SecureLogEntry entry = SecureLogEntry.create(
                newIndex,
                ts,
                eventType,
                severity,
                message,
                lastHash
        );

        String line = entry.toLine() + System.lineSeparator();
        Files.writeString(
                logFile,
                line,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );

        this.lastIndex = newIndex;
        this.lastHash = entry.getHash();

        return entry;
    }

    public Path getLogFile() {
        return logFile;
    }

    public static VerificationResult verifyLogFile(Path logFile) throws IOException {
        if (!Files.exists(logFile)) {
            return new VerificationResult(false, 0, null, "File does not exist");
        }

        long total = 0L;
        long expectedIndex = 0L;
        String expectedPrevHash = SecureLogEntry.GENESIS_HASH;

        try (BufferedReader reader = Files.newBufferedReader(logFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                total++;

                SecureLogEntry entry = SecureLogEntry.fromLine(line);

                long index = entry.getIndex();
                if (index != expectedIndex + 1) {
                    return new VerificationResult(
                            false,
                            total,
                            index,
                            "Index sequence broken: expected " + (expectedIndex + 1) + " but got " + index
                    );
                }

                if (!expectedPrevHash.equals(entry.getPrevHash())) {
                    return new VerificationResult(
                            false,
                            total,
                            index,
                            "prevHash mismatch at index " + index
                    );
                }

                String recomputedHash = SecureLogEntry.recomputeHash(
                        entry.getIndex(),
                        entry.getTimestampMillis(),
                        entry.getEventType(),
                        entry.getSeverity(),
                        entry.getMessage(),
                        entry.getPrevHash()
                );

                if (!recomputedHash.equals(entry.getHash())) {
                    return new VerificationResult(
                            false,
                            total,
                            index,
                            "Hash mismatch at index " + index
                    );
                }

                expectedIndex = index;
                expectedPrevHash = entry.getHash();
            }
        }

        return new VerificationResult(true, total, null, "OK");
    }
}
