package com.guardian.modules;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Записывает события в локальный лог-файл.
 * Подходит для SA-машин без сети.
 */
public class FileLocalServiceClient implements LocalServiceClient {

    private final Path logFile;

    public FileLocalServiceClient(String logFilePath) {
        this.logFile = Paths.get(logFilePath);
    }

    @Override
    public synchronized void sendEvent(SecurityEvent event) {
        try {
            if (logFile.getParent() != null) {
                Files.createDirectories(logFile.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(
                    logFile,
                    java.nio.charset.StandardCharsets.UTF_8,
                    Files.exists(logFile)
                            ? java.nio.file.StandardOpenOption.APPEND
                            : java.nio.file.StandardOpenOption.CREATE
            )) {
                writer.write(formatEvent(event));
                writer.newLine();
            }
        } catch (IOException e) {
            // чтобы не зациклиться на логах, просто игнорируем ошибку
        }
    }

    private String formatEvent(SecurityEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getTimestamp()).append(" | ");
        sb.append(event.getType()).append(" | ");
        sb.append(event.getMessage());
        if (!event.getDetails().isEmpty()) {
            sb.append(" | ");
            sb.append(event.getDetails());
        }
        return sb.toString();
    }
}
