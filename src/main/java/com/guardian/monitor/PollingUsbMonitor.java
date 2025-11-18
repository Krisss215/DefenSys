package com.guardian.monitor;

import com.guardian.modules.FileScanner;
import com.guardian.modules.LocalServiceClient;
import com.guardian.modules.SecurityEvent;
import com.guardian.modules.SecurityEventType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Периодически опрашивает систему и ищет новые USB-устройства.
 * На Windows:
 *  - DISK:...  -> съёмные диски (флешки, внешние HDD) + сканирование файлов
 *  - DEV:...   -> любые USB-устройства (телефон, мышь и т.п.), без сканирования файлов
 *
 * На macOS / Linux логика: отслеживание смонтированных томов.
 */
public class PollingUsbMonitor implements UsbMonitor {

    private final LocalServiceClient client;
    private final FileScanner fileScanner;
    private final long pollIntervalMillis;

    private volatile boolean running;
    private Thread worker;
    private final Set<String> knownDevices = new HashSet<>();

    public PollingUsbMonitor(LocalServiceClient client, FileScanner fileScanner, long pollIntervalMillis) {
        this.client = client;
        this.fileScanner = fileScanner;
        this.pollIntervalMillis = pollIntervalMillis;
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        worker = new Thread(this::loop, "UsbMonitorThread");
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
                Set<String> current = detectUsbDevices();

                for (String dev : current) {
                    if (!knownDevices.contains(dev)) {
                        handleNewDevice(dev);
                    }
                }
                for (String dev : new HashSet<>(knownDevices)) {
                    if (!current.contains(dev)) {
                        handleRemovedDevice(dev);
                    }
                }

                knownDevices.clear();
                knownDevices.addAll(current);

                Thread.sleep(pollIntervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                Map<String, String> details = new HashMap<>();
                details.put("error", e.getMessage());
                client.sendEvent(new SecurityEvent(
                        SecurityEventType.INTERNAL_ERROR,
                        "Ошибка в USB-мониторе",
                        details
                ));
            }
        }
    }

    /**
     * Возвращает множество идентификаторов устройств:
     *  - "DISK:..." -> съёмные диски
     *  - "DEV:..."  -> любые USB-девайсы (Windows)
     */
    private Set<String> detectUsbDevices() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return detectUsbWindows();
        } else if (os.contains("mac") || os.contains("darwin")) {
            return detectUsbMac();
        } else {
            return detectUsbLinux();
        }
    }

    private Set<String> detectUsbWindows() {
        Set<String> result = new HashSet<>();
        result.addAll(detectRemovableDisksWindows());
        result.addAll(detectUsbPnPDevicesWindows());
        return result;
    }

    /**
     * Windows: съёмные диски (DriveType=2)
     */
    private Set<String> detectRemovableDisksWindows() {
        Set<String> result = new HashSet<>();
        try {
            Process process = new ProcessBuilder("wmic", "logicaldisk", "get", "DeviceID,DriveType")
                    .redirectErrorStream(true)
                    .start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("DeviceID")) {
                        continue;
                    }
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        String deviceId = parts[0].trim();    // E:
                        String driveType = parts[1].trim();   // 2 = removable
                        if ("2".equals(driveType)) {
                            result.add("DISK:" + deviceId);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    /**
     * Windows: список PnP-устройств класса USB (телефон и т.п.)
     */
    private Set<String> detectUsbPnPDevicesWindows() {
        Set<String> result = new HashSet<>();
        try {
            Process process = new ProcessBuilder(
                    "wmic",
                    "path",
                    "Win32_PnPEntity",
                    "where",
                    "Name like '%USB%'",
                    "get",
                    "Name"
            ).redirectErrorStream(true).start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("Name")) {
                        continue;
                    }
                    result.add("DEV:" + line);
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    /**
     * macOS: внешние тома в /Volumes
     */
    private Set<String> detectUsbMac() {
        Set<String> result = new HashSet<>();
        Path volumes = Paths.get("/Volumes");
        java.io.File dir = volumes.toFile();
        java.io.File[] children = dir.listFiles();
        if (children != null) {
            for (java.io.File child : children) {
                if (child.isDirectory()) {
                    result.add("DISK:" + child.getName());
                }
            }
        }
        return result;
    }

    /**
     * Linux: эвристика с /media и /run/media
     */
    private Set<String> detectUsbLinux() {
        Set<String> result = new HashSet<>();
        addChildren(result, "/media");
        addChildren(result, "/run/media");
        return result;
    }

    private void addChildren(Set<String> result, String rootPath) {
        java.io.File dir = new java.io.File(rootPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        java.io.File[] children = dir.listFiles();
        if (children != null) {
            for (java.io.File child : children) {
                if (child.isDirectory()) {
                    result.add("DISK:" + child.getAbsolutePath());
                }
            }
        }
    }

    private void handleNewDevice(String deviceKey) {
        Map<String, String> details = new HashMap<>();

        if (deviceKey.startsWith("DISK:")) {
            String id = deviceKey.substring("DISK:".length());
            details.put("deviceId", id);
            details.put("kind", "DISK");

            client.sendEvent(new SecurityEvent(
                    SecurityEventType.USB_CONNECTED,
                    "Подключён новый USB-носитель",
                    details
            ));

            Path rootPath = resolveRootPath(id);
            if (rootPath != null) {
                fileScanner.scanPath(rootPath);
            }
        } else if (deviceKey.startsWith("DEV:")) {
            String name = deviceKey.substring("DEV:".length());
            details.put("deviceName", name);
            details.put("kind", "DEVICE");

            client.sendEvent(new SecurityEvent(
                    SecurityEventType.USB_CONNECTED,
                    "Подключено новое USB-устройство",
                    details
            ));
        } else {
            details.put("raw", deviceKey);
            client.sendEvent(new SecurityEvent(
                    SecurityEventType.USB_CONNECTED,
                    "Обнаружено новое USB-устройство",
                    details
            ));
        }
    }

    private void handleRemovedDevice(String deviceKey) {
        Map<String, String> details = new HashMap<>();
        if (deviceKey.startsWith("DISK:")) {
            details.put("kind", "DISK");
            details.put("deviceId", deviceKey.substring("DISK:".length()));
        } else if (deviceKey.startsWith("DEV:")) {
            details.put("kind", "DEVICE");
            details.put("deviceName", deviceKey.substring("DEV:".length()));
        } else {
            details.put("raw", deviceKey);
        }

        client.sendEvent(new SecurityEvent(
                SecurityEventType.USB_DISCONNECTED,
                "USB-устройство отключено",
                details
        ));
    }

    private Path resolveRootPath(String diskId) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return Paths.get(diskId + "\\");
        } else if (os.contains("mac") || os.contains("darwin")) {
            return Paths.get("/Volumes").resolve(diskId);
        } else {
            return Paths.get(diskId);
        }
    }
}
