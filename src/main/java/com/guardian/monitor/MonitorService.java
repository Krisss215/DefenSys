// MonitorService.java
package com.guardian.monitor;

import com.guardian.ui.DashboardPanel;
import com.guardian.ui.HistoryPanel;
import com.guardian.ui.StatusPanel;

import javax.swing.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Связка между бэкенд-мониторами и UI.
 * Сейчас содержит демо-логику старт/стоп, позже можно
 * добавить реальные потоки NetworkMonitor/UsbMonitor.
 */
public class MonitorService implements SecurityEventListener {

    private final StatusPanel statusPanel;
    private final DashboardPanel dashboardPanel;
    private final HistoryPanel historyPanel;

    private volatile boolean running = false;

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public MonitorService(StatusPanel statusPanel,
                          DashboardPanel dashboardPanel,
                          HistoryPanel historyPanel) {
        this.statusPanel = statusPanel;
        this.dashboardPanel = dashboardPanel;
        this.historyPanel = historyPanel;
    }

    public void startAgent() {
        if (running) return;
        running = true;

        SwingUtilities.invokeLater(() -> {
            historyPanel.addEvent(
                    now(), "Agent", "Monitoring started", false
            );
            // демо-состояние – можешь убрать и подставить реальные данные
            statusPanel.setNetworkOnline(true, 3);
            statusPanel.setUsbStats(1, 0);
            dashboardPanel.updateStats(12, 0, 12,
                    new double[]{1, 2, 1, 3, 2, 4, 3, 5});
        });
    }

    public void stopAgent() {
        if (!running) return;
        running = false;

        SwingUtilities.invokeLater(() -> {
            historyPanel.addEvent(
                    now(), "Agent", "Monitoring stopped", false
            );
            statusPanel.setNetworkOnline(false, 0);
            statusPanel.setUsbStats(0, 0);
        });
    }

    private String now() {
        return LocalTime.now().format(TIME_FMT);
    }

    @Override
    public void onNetworkChanged(boolean online, int activeConnections) {
        if (!running) return;
        SwingUtilities.invokeLater(() ->
                statusPanel.setNetworkOnline(online, activeConnections)
        );
    }

    @Override
    public void onUsbActivity(int totalEvents, int suspiciousEvents) {
        if (!running) return;
        SwingUtilities.invokeLater(() ->
                statusPanel.setUsbStats(totalEvents, suspiciousEvents)
        );
    }

    @Override
    public void onStatsUpdated(int totalEvents, int blocked, int safeSessions, double[] trend) {
        if (!running) return;
        SwingUtilities.invokeLater(() ->
                dashboardPanel.updateStats(totalEvents, blocked, safeSessions, trend)
        );
    }
}
