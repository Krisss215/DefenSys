package com.guardian.monitor;

import com.guardian.modules.FileScanner;
import com.guardian.modules.LocalServiceClient;
import com.guardian.modules.SecurityEvent;
import com.guardian.modules.SecurityEventType;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * Отслеживает новые файлы в заданных каталогах через WatchService.
 */
public class WatchServiceFileActivityMonitor implements FileActivityMonitor {

    private final List<Path> directories;
    private final FileScanner fileScanner;
    private final LocalServiceClient client;

    private volatile boolean running;
    private Thread worker;
    private WatchService watchService;

    public WatchServiceFileActivityMonitor(List<Path> directories,
                                           FileScanner fileScanner,
                                           LocalServiceClient client) {
        this.directories = directories;
        this.fileScanner = fileScanner;
        this.client = client;
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        worker = new Thread(this::loop, "FileActivityMonitorThread");
        worker.setDaemon(true);
        worker.start();
    }

    @Override
    public synchronized void stop() {
        running = false;
        if (worker != null) {
            worker.interrupt();
        }
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void loop() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Map<WatchKey, Path> keyToDir = new HashMap<>();

            for (Path dir : directories) {
                if (dir != null && Files.isDirectory(dir)) {
                    WatchKey key = dir.register(watchService, ENTRY_CREATE);
                    keyToDir.put(key, dir);
                }
            }

            while (running) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (ClosedWatchServiceException e) {
                    break;
                }

                Path dir = keyToDir.get(key);
                if (dir != null) {
                    // ВАЖНО: используем java.nio.file.WatchEvent явно
                    for (java.nio.file.WatchEvent<?> event : key.pollEvents()) {
                        java.nio.file.WatchEvent.Kind<?> kind = event.kind();
                        if (kind == ENTRY_CREATE) {
                            java.nio.file.WatchEvent<Path> ev = cast(event);
                            Path filename = ev.context();
                            Path child = dir.resolve(filename);

                            // сканируем новый файл
                            fileScanner.scanFile(child);

                            Map<String, String> details = new HashMap<>();
                            details.put("path", child.toAbsolutePath().toString());

                            client.sendEvent(new SecurityEvent(
                                    SecurityEventType.FILE_CREATED,
                                    "Создан новый файл",
                                    details
                            ));
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    keyToDir.remove(key);
                    if (keyToDir.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            Map<String, String> details = new HashMap<>();
            details.put("error", e.getMessage());
            client.sendEvent(new SecurityEvent(
                    SecurityEventType.INTERNAL_ERROR,
                    "Ошибка в файловом мониторе",
                    details
            ));
        }
    }

    @SuppressWarnings("unchecked")
    private static java.nio.file.WatchEvent<Path> cast(java.nio.file.WatchEvent<?> event) {
        return (java.nio.file.WatchEvent<Path>) event;
    }
}
