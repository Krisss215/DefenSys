package com.guardian.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class SecurityEventLogger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    private static final File LOG_FILE = resolveLogFile();

    private SecurityEventLogger() {
    }

    private static File resolveLogFile() {
        String userHome = System.getProperty("user.home", ".");
        File dir = new File(userHome, "ElderGuardianLogs");
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return new File(dir, "security-events.log");
    }

    public static synchronized void log(String type, String message) {
        String timestamp = FORMATTER.format(Instant.now());
        String line = String.format("%s [%s] %s%n", timestamp, type, message);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write(line);
        } catch (IOException e) {
            // не роняем агент из-за проблем с логом
            e.printStackTrace();
        }
    }

    public static File getLogFile() {
        return LOG_FILE;
    }
}

