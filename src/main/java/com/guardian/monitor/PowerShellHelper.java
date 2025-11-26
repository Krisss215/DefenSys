package com.guardian.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Helper to execute PowerShell commands and return their output.
 * The helper:
 *  - runs PowerShell without profile
 *  - suppresses PowerShell error stream (2>$null)
 *  - throws IOException with short EN message when exit code != 0
 *  - returns trimmed stdout as a single string
 */
public final class PowerShellHelper {

    private static final Charset PS_CHARSET = StandardCharsets.UTF_8;

    private PowerShellHelper() {
    }

    /**
     * Executes given PowerShell script and returns its stdout as String.
     *
     * @param script PowerShell script body (without & { } wrapper)
     * @return stdout text (trimmed)
     * @throws IOException if PowerShell returns non-zero exit code or I/O fails
     */
    public static String run(String script) throws IOException {
        // Suppress PowerShell error stream to avoid localized error text in logs
        String wrappedScript = "& { " + script + " } 2>$null";

        ProcessBuilder pb = new ProcessBuilder(
                "powershell.exe",
                "-NoProfile",
                "-ExecutionPolicy", "Bypass",
                "-Command",
                wrappedScript
        );
        // We already redirected errors inside script (2>$null), so here we just read stdout
        pb.redirectErrorStream(true);

        Process process = pb.start();

        String output;
        try (InputStream is = process.getInputStream();
             InputStreamReader isr = new InputStreamReader(is, PS_CHARSET);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            output = sb.toString().trim();
        }

        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("PowerShell command interrupted", e);
        }

        if (exitCode != 0) {
            // Short, English-only message – no raw PowerShell error text
            throw new IOException("PowerShell command failed (exitCode=" + exitCode + ")");
        }

        return output;
    }
}
