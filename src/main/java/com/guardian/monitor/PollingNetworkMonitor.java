package com.guardian.monitor;

import com.guardian.modules.LocalServiceClient;
import com.guardian.modules.SecurityEvent;
import com.guardian.modules.SecurityEventType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Периодически запускает netstat и ищет новые соединения.
 */
public class PollingNetworkMonitor implements NetworkMonitor {

    private final LocalServiceClient client;
    private final long pollIntervalMillis;

    private volatile boolean running;
    private Thread worker;
    private final Set<String> knownConnections = new HashSet<>();

    public PollingNetworkMonitor(LocalServiceClient client, long pollIntervalMillis) {
        this.client = client;
        this.pollIntervalMillis = pollIntervalMillis;
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        worker = new Thread(this::loop, "NetworkMonitorThread");
        worker.setDaemon(true);
        worker.start();
    }

    @Override
    public synchronized void stop() {
        running = false;
        if (worker != null) {
            worker.interrupt();
        }
    }

    private void loop() {
        while (running) {
            try {
                Set<String> current = captureConnections();
                for (String conn : current) {
                    if (!knownConnections.contains(conn)) {
                        handleNewConnection(conn);
                    }
                }
                knownConnections.clear();
                knownConnections.addAll(current);

                Thread.sleep(pollIntervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                Map<String, String> details = new HashMap<>();
                details.put("error", e.getMessage());
                client.sendEvent(new SecurityEvent(
                        SecurityEventType.INTERNAL_ERROR,
                        "Error",
                        details
                ));
            }
        }
    }

    private Set<String> captureConnections() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return captureWindows();
        } else {
            return captureUnix();
        }
    }

    private Set<String> captureWindows() {
        return runNetstat(new String[]{"netstat", "-ano"});
    }

    private Set<String> captureUnix() {
        return runNetstat(new String[]{"netstat", "-an"});
    }

    private Set<String> runNetstat(String[] command) {
        Set<String> result = new HashSet<>();
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (line.startsWith("TCP") || line.startsWith("UDP")) {
                        result.add(line);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    private void handleNewConnection(String rawLine) {
        Map<String, String> details = new HashMap<>();
        details.put("connection", rawLine);

        client.sendEvent(new SecurityEvent(
                SecurityEventType.NETWORK_NEW_CONNECTION,
                "New connection",
                details
        ));
    }
}
