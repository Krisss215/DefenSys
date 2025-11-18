package com.guardian.modules;

import com.guardian.monitor.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Центральный backend-класс, который поднимает все мониторы.
 * UI может просто дергать startAgent()/stopAgent(), не меняя дизайн.
 */
public class MonitoringManager {

    private final AgentStatusListener statusListener;

    private final LocalServiceClient baseClient;
    private final UiAwareLocalServiceClient uiClient;
    private final FileScanner fileScanner;

    private final UsbMonitor usbMonitor;
    private final NetworkMonitor networkMonitor;
    private final FileActivityMonitor fileActivityMonitor;

    private boolean started = false;

    public MonitoringManager(AgentStatusListener statusListener) {
        this.statusListener = statusListener;

        LocalServiceConfig config = LocalServiceConfig.loadDefault();
        this.baseClient = LocalServiceClientFactory.create(config);
        this.uiClient = new UiAwareLocalServiceClient(baseClient, statusListener);
        this.fileScanner = new SimpleFileScanner(uiClient);

        this.usbMonitor = new PollingUsbMonitor(uiClient, fileScanner, 5_000L);
        this.networkMonitor = new PollingNetworkMonitor(uiClient, 5_000L);

        List<Path> dirs = buildDefaultWatchDirs();
        this.fileActivityMonitor = new WatchServiceFileActivityMonitor(dirs, fileScanner, uiClient);
    }

    private List<Path> buildDefaultWatchDirs() {
        List<Path> dirs = new ArrayList<>();
        String home = System.getProperty("user.home", ".");
        dirs.add(Paths.get(home, "Downloads"));
        dirs.add(Paths.get(home, "Desktop"));
        dirs.add(Paths.get(home, "Documents"));
        return dirs;
    }

    public synchronized void startAgent() {
        if (started) {
            return;
        }
        started = true;
        usbMonitor.start();
        networkMonitor.start();
        fileActivityMonitor.start();
        if (statusListener != null) {
            statusListener.onAgentStarted();
            statusListener.onUsbStatsChanged(0, 0);
            statusListener.onNetworkStatsChanged(0);
        }
    }

    public synchronized void stopAgent() {
        if (!started) {
            return;
        }
        started = false;
        usbMonitor.stop();
        networkMonitor.stop();
        fileActivityMonitor.stop();
        if (statusListener != null) {
            statusListener.onAgentStopped();
        }
    }

    public LocalServiceClient getLocalServiceClient() {
        return uiClient;
    }
}
