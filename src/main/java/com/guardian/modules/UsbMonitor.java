package com.guardian.modules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import com.guardian.logging.LoggerModule;


public class UsbMonitor {
    public interface UsbListener {
        void onDevicesChanged(List<String> currentDevices, List<String> added, List<String> removed);
    }

    private static final List<UsbListener> listeners = new CopyOnWriteArrayList<>();
    private static Set<String> previous = new HashSet<>();

    public static void addListener(UsbListener l) { listeners.add(l); }
    public static void removeListener(UsbListener l) { listeners.remove(l); }

    // call this to start background polling
    public static void startPolling(long intervalMillis) {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Set<String> cur = queryRemovableDrives();
                    List<String> added = new ArrayList<>();
                    List<String> removed = new ArrayList<>();
                    for (String s : cur) if (!previous.contains(s)) added.add(s);
                    for (String s : previous) if (!cur.contains(s)) removed.add(s);

                    if (!added.isEmpty() || !removed.isEmpty()) {
                        LoggerModule.log("USB: added=" + added + " removed=" + removed);
                        for (UsbListener l : listeners) {
                            try { l.onDevicesChanged(new ArrayList<>(cur), added, removed); } catch (Throwable ignore) {}
                        }
                    }
                    previous = cur;
                    Thread.sleep(intervalMillis);
                } catch (Throwable e) {
                    LoggerModule.log("UsbMonitor error: " + e.getMessage());
                    try { Thread.sleep(intervalMillis); } catch (InterruptedException ignore) {}
                }
            }
        }, "UsbMonitor");
        t.setDaemon(true);
        t.start();
    }

    // Simple Windows wmic approach
    private static Set<String> queryRemovableDrives() {
        Set<String> out = new HashSet<>();
        try {
            Process p = new ProcessBuilder("cmd", "/c", "wmic logicaldisk where \"DriveType=2\" get DeviceID,VolumeName /format:csv")
                    .redirectErrorStream(true)
                    .start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("Node")) continue;
                    // CSV format: Node,DeviceID,VolumeName
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String device = parts[1].trim();
                        String label = parts[2].trim();
                        if (device.length() > 0) {
                            out.add(device + (label.isEmpty() ? "" : " (" + label + ")"));
                        }
                    }
                }
            }
            p.waitFor();
        } catch (Exception e) {
            LoggerModule.log("UsbMonitor query failed: " + e.getMessage());
        }
        return out;
    }
}

