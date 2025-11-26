package com.guardian.monitor;

import com.guardian.logging.SecureLogManager;

import java.nio.file.*;
import java.util.List;

/**
 * Monitors file system activity (new files) in selected folders.
 */
public class FileActivityMonitor implements Runnable {

    private final SecurityScanner scanner = new SecurityScanner();

    @Override
    public void run() {
        SecureLogManager.getInstance().logEvent(
                "FILES",
                "FileActivityMonitor",
                "File activity monitor started."
        );

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            List<Path> pathsToWatch = List.of(
                    Paths.get(System.getProperty("user.home"), "Desktop"),
                    Paths.get(System.getProperty("user.home"), "Downloads"),
                    Paths.get(System.getProperty("user.home"), "Documents")
            );

            for (Path path : pathsToWatch) {
                try {
                    path.toFile().mkdirs();
                    path.register(
                            watchService,
                            StandardWatchEventKinds.ENTRY_CREATE
                    );
                    SecureLogManager.getInstance().logEvent(
                            "FILES",
                            "FileActivityMonitor",
                            "Watching folder: " + path
                    );
                } catch (Exception e) {
                    SecureLogManager.getInstance().logEvent(
                            "FILES",
                            "FileActivityMonitor",
                            "Failed to watch folder " + path + ": " + e.getMessage()
                    );
                }
            }

            while (!Thread.currentThread().isInterrupted()) {
                WatchKey key = watchService.take();
                Path dir = (Path) key.watchable();

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fullPath = dir.resolve(ev.context());

                    SecureLogManager.getInstance().logEvent(
                            "FILES",
                            "FileActivityMonitor",
                            "New file detected: " + fullPath
                    );

                    scanner.scanFile(fullPath.toFile());
                }

                if (!key.reset()) {
                    break;
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            SecureLogManager.getInstance().logEvent(
                    "FILES",
                    "FileActivityMonitor",
                    "File activity monitor stopped with error: " + e.getMessage()
            );
        }
    }
}
