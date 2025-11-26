package com.guardian.monitor;

import com.guardian.modules.MonitoringManager;
import com.guardian.ui.HistoryPanel;

/**
 * Lightweight bridge between UI and monitoring backend.
 * Old code can still create MonitorService with a HistoryPanel
 * and call start()/stop(), but all real work is delegated to
 * MonitoringManager / ExtendedMonitoringBootstrap.
 */
public class MonitorService {

    private final HistoryPanel historyPanel;
    private boolean running = false;

    /**
     * UI passes HistoryPanel so we can log basic events there.
     */
    public MonitorService(HistoryPanel historyPanel) {
        this.historyPanel = historyPanel;
    }

    /**
     * Starts background monitoring (USB, network, files, etc.).
     */
    public synchronized void start() {
        if (running) {
            return;
        }
        MonitoringManager.getInstance().start();
        running = true;

        if (historyPanel != null) {
            historyPanel.addEvent("INFO", "Agent", "Monitoring started", false);
        }
    }

    /**
     * Stops monitoring (currently a no-op for background threads,
     * but kept for backward compatibility).
     */
    public synchronized void stop() {
        if (!running) {
            return;
        }
        MonitoringManager.getInstance().stop();
        running = false;

        if (historyPanel != null) {
            historyPanel.addEvent("INFO", "Agent", "Monitoring stopped", false);
        }
    }

    /**
     * Convenience flag for UI.
     */
    public boolean isRunning() {
        return running;
    }
}
