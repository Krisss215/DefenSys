package com.guardian.monitor;

import com.guardian.ui.DashboardPanel;
import com.guardian.ui.HistoryPanel;
import com.guardian.ui.StatusPanel;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Simple cross-platform USB monitor:
 *  - macOS: watches /Volumes
 *  - Windows: watches drive letters (File.listRoots)
 *
 *  It treats ALL new drives (after start) as "USB/removable".
 */
public class MonitorService {

    private final DashboardPanel dashboardPanel;
    private final HistoryPanel historyPanel;
    private final StatusPanel statusPanel;

    private volatile boolean running = false;

    private final String osName;
    private final boolean isMac;
    private final boolean isWindows;

    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");

    // “known” devices at previous iteration
    private Set<String> knownDevices = new HashSet<>();

    // stats for chart
    private final List<Double> usbTimeline = new ArrayList<>();
    private int usbEvents = 0;

    public MonitorService(DashboardPanel dashboardPanel) {
        this.dashboardPanel = dashboardPanel;
        this.historyPanel = dashboardPanel.getHistoryPanel();
        this.statusPanel = dashboardPanel.getStatusPanel();

        osName = System.getProperty("os.name", "").toLowerCase();
        isMac = osName.contains("mac");
        isWindows = osName.contains("win");
    }

    public void startAgent() {
        if (running) return;
        running = true;

        SwingUtilities.invokeLater(() -> {
            statusPanel.setRunning(true);
            historyPanel.addEvent(now(), "Agent", "Monitoring started", false);
            statusPanel.setNetworkOnline(true, 0);
            statusPanel.setUsbStats(0, 0);
            updateChart();
        });

        // baseline: devices that уже подключены в момент старта — считаем “нормальными”
        knownDevices = detectCurrentDevices();

        Thread watcher = new Thread(this::watchLoop, "USB-Watcher");
        watcher.setDaemon(true);
        watcher.start();
    }

    public void stopAgent() {
        running = false;
        SwingUtilities.invokeLater(() -> {
            statusPanel.setRunning(false);
            historyPanel.addEvent(now(), "Agent", "Monitoring stopped", false);
        });
    }

    private String now() {
        return LocalTime.now().format(timeFmt);
    }

    private void watchLoop() {
        while (running) {
            try {
                Set<String> current = detectCurrentDevices();

                // новые устройства
                for (String dev : current) {
                    if (!knownDevices.contains(dev)) {
                        onDeviceConnected(dev);
                    }
                }

                // отключённые устройства
                for (String dev : knownDevices) {
                    if (!current.contains(dev)) {
                        onDeviceRemoved(dev);
                    }
                }

                knownDevices = current;

                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            } catch (Exception ex) {
                String msg = "Monitor error: " + ex.getMessage();
                SwingUtilities.invokeLater(() ->
                        historyPanel.addEvent(now(), "Agent", msg, true)
                );
            }
        }
    }

    /** Детект текущих “томов” в системе */
    private Set<String> detectCurrentDevices() {
        Set<String> result = new HashSet<>();

        if (isMac) {
            File volumesDir = new File("/Volumes");
            File[] files = volumesDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    String name = f.getName();
                    // игнорируем системные штуки
                    if (name.startsWith(".") || "Macintosh HD".equalsIgnoreCase(name)) {
                        continue;
                    }
                    result.add("MAC_VOL:" + name);
                }
            }
        } else if (isWindows) {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            File[] roots = File.listRoots();
            if (roots != null) {
                for (File root : roots) {
                    try {
                        String path = root.getAbsolutePath();  // типа "E:\"
                        // отбрасываем диски с нулевым размером
                        if (root.getTotalSpace() <= 0) continue;

                        // считаем всё, что появилось после старта, “removable”
                        if (fsv.isDrive(root)) {
                            result.add("WIN_DRIVE:" + path);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        } else {
            // другие ОС не мониторим
        }

        return result;
    }

    private void onDeviceConnected(String id) {
        usbEvents++;

        SwingUtilities.invokeLater(() -> {
            String name = prettyName(id);
            historyPanel.addEvent(now(), "USB", "Device connected: " + name, false);
            statusPanel.setUsbStats(usbEvents, 0);
            addUsbEventToTimeline();
            updateChart();
        });
    }

    private void onDeviceRemoved(String id) {
        SwingUtilities.invokeLater(() -> {
            String name = prettyName(id);
            historyPanel.addEvent(now(), "USB", "Device removed: " + name, false);
        });
    }

    private String prettyName(String internal) {
        if (internal.startsWith("MAC_VOL:")) {
            return internal.substring("MAC_VOL:".length());
        }
        if (internal.startsWith("WIN_DRIVE:")) {
            return internal.substring("WIN_DRIVE:".length());
        }
        return internal;
    }

    private void addUsbEventToTimeline() {
        if (usbTimeline.isEmpty()) {
            usbTimeline.add(1.0);
        } else {
            int last = usbTimeline.size() - 1;
            usbTimeline.set(last, usbTimeline.get(last) + 1.0);
        }
        // ограничим длину, чтобы график не разрастался
        if (usbTimeline.size() > 20) {
            usbTimeline.remove(0);
        }
    }

    private void updateChart() {
        double[] arr = usbTimeline.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        dashboardPanel.updateStats(usbEvents, 0, 0, arr);
    }
}
