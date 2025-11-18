package com.guardian.modules;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Простой офлайн-сканер.
 * Ищет подозрительные расширения и отправляет события.
 */
public class SimpleFileScanner implements FileScanner {

    private final LocalServiceClient client;
    private final Set<String> suspiciousExtensions;

    public SimpleFileScanner(LocalServiceClient client) {
        this.client = client;
        this.suspiciousExtensions = new HashSet<>(Arrays.asList(
                "exe", "bat", "cmd", "vbs", "ps1", "js", "jar", "scr", "msi", "reg"
        ));
    }

    @Override
    public void scanPath(Path root) {
        if (root == null || !Files.exists(root)) {
            return;
        }

        try {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    scanFile(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            Map<String, String> details = new HashMap<>();
            details.put("path", root.toString());
            details.put("error", e.getMessage());
            client.sendEvent(new SecurityEvent(
                    SecurityEventType.INTERNAL_ERROR,
                    "Ошибка при обходе каталога",
                    details
            ));
        }
    }

    @Override
    public void scanFile(Path file) {
        if (file == null || !Files.isRegularFile(file)) {
            return;
        }

        String fileName = file.getFileName().toString();
        String ext = extractExtension(fileName);

        Map<String, String> details = new HashMap<>();
        details.put("path", file.toAbsolutePath().toString());
        if (ext != null) {
            details.put("extension", ext);
        }

        if (ext != null && suspiciousExtensions.contains(ext.toLowerCase(Locale.ROOT))) {
            client.sendEvent(new SecurityEvent(
                    SecurityEventType.FILE_SCANNED_SUSPICIOUS,
                    "Найден подозрительный файл",
                    details
            ));
        } else {
            client.sendEvent(new SecurityEvent(
                    SecurityEventType.FILE_SCANNED_CLEAN,
                    "Файл просканирован",
                    details
            ));
        }
    }

    private String extractExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot <= 0 || dot == name.length() - 1) {
            return null;
        }
        return name.substring(dot + 1);
    }
}

