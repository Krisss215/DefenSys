package com.guardian.monitor;

import com.guardian.logging.SecureLogManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Set;

/**
 * Simple security scanner for new files.
 * - Calculates SHA-256 hash.
 * - Checks extension against a small list of risky types.
 * - Logs result to SecureLogManager.
 *
 * This is NOT a real antivirus, but it gives visibility
 * for all incoming files.
 */
public class SecurityScanner {

    // Very small example list of risky extensions
    private static final Set<String> SUSPICIOUS_EXTENSIONS = Set.of(
            ".exe", ".dll", ".bat", ".cmd", ".vbs", ".js", ".ps1"
    );

    /**
     * Scans single file and writes result to security log.
     *
     * @param file file to scan
     */
    public void scanFile(File file) {
        if (file == null) {
            return;
        }

        try {
            if (!file.exists() || !file.isFile()) {
                SecureLogManager.getInstance().logEvent(
                        "SCAN",
                        "SecurityScanner",
                        "File does not exist or is not a regular file: " + file.getAbsolutePath()
                );
                return;
            }

            long size = file.length();
            String extension = getExtension(file.getName());
            String hash = calculateSha256(file);

            boolean suspicious = isSuspiciousExtension(extension);

            StringBuilder msg = new StringBuilder();
            msg.append("File scanned: ")
                    .append(file.getAbsolutePath())
                    .append(" | size=")
                    .append(size)
                    .append(" bytes")
                    .append(" | extension='")
                    .append(extension)
                    .append("'")
                    .append(" | sha256=")
                    .append(hash);

            if (suspicious) {
                msg.append(" (SUSPECT: risky file extension)");
            }

            SecureLogManager.getInstance().logEvent(
                    "SCAN",
                    "SecurityScanner",
                    msg.toString()
            );

        } catch (Exception e) {
            SecureLogManager.getInstance().logEvent(
                    "SCAN",
                    "SecurityScanner",
                    "Failed to scan file " + file.getAbsolutePath() + ": " + e.getMessage()
            );
        }
    }

    private String getExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot == -1) {
            return "";
        }
        return name.substring(dot).toLowerCase();
    }

    private boolean isSuspiciousExtension(String ext) {
        if (ext == null) return false;
        return SUSPICIOUS_EXTENSIONS.contains(ext.toLowerCase());
    }

    private String calculateSha256(File file) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(file.toPath());
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        return HexFormat.of().formatHex(hash);
    }
}
