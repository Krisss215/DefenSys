package com.guardian.monitor;

import com.guardian.logging.SecureLogManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UsbMonitorService implements Runnable {

    private static final long POLL_INTERVAL_MS = 4000L;

    private final Map<String, String> knownDevices = new HashMap<>();

    @Override
    public void run() {
        SecureLogManager.getInstance().logEvent(
                "USB",
                "UsbMonitorService",
                "USB monitor started."
        );

        while (!Thread.currentThread().isInterrupted()) {
            try {
                checkUsbDevices();
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                SecureLogManager.getInstance().logEvent(
                        "USB",
                        "UsbMonitorService",
                        "Error while polling USB devices: " + e.getMessage()
                );
            }
        }
    }

    private void checkUsbDevices() throws IOException {
        String csv = PowerShellHelper.run(
                "$devices = Get-PnpDevice | " +
                        "Where-Object { $_.Status -eq 'OK' -and " +
                        " ($_.InstanceId -like 'USB*' -or $_.Class -eq 'USB' -or $_.Class -eq 'WPD') }; " +
                        "$devices | " +
                        "Select-Object InstanceId,FriendlyName,Manufacturer | " +
                        "ConvertTo-Csv -NoTypeInformation"
        );

        Map<String, String> current = new HashMap<>();
        String[] lines = csv.split("\\r?\\n");

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",", 3);
            if (parts.length < 3) continue;

            String id = unquote(parts[0]);
            String friendlyName = unquote(parts[1]);
            String manufacturer = unquote(parts[2]);

            String description = "manufacturer='" + manufacturer + "', name='" + friendlyName + "'";
            current.put(id, description);
        }

        Set<String> newDevices = new HashSet<>(current.keySet());
        newDevices.removeAll(knownDevices.keySet());

        Set<String> removedDevices = new HashSet<>(knownDevices.keySet());
        removedDevices.removeAll(current.keySet());

        for (String id : newDevices) {
            String description = current.get(id);
            boolean maybeChinese = isProbablyChinese(description);

            StringBuilder msg = new StringBuilder("New USB device: ")
                    .append(id)
                    .append(", ")
                    .append(description);

            if (maybeChinese) {
                msg.append(" (SUSPECT: possible Chinese manufacturer)");
            }

            SecureLogManager.getInstance().logEvent(
                    "USB",
                    "UsbMonitorService",
                    msg.toString()
            );
        }

        for (String id : removedDevices) {
            String description = knownDevices.get(id);
            SecureLogManager.getInstance().logEvent(
                    "USB",
                    "UsbMonitorService",
                    "USB device disconnected: " + id + ", " + description
            );
        }

        knownDevices.clear();
        knownDevices.putAll(current);
    }

    private boolean isProbablyChinese(String text) {
        if (text == null) return false;
        String m = text.toLowerCase();
        return m.contains("shenzhen")
                || m.contains("china")
                || m.contains("hong kong")
                || m.contains("cn")
                || m.contains("aliexpress");
    }

    private String unquote(String s) {
        String result = s.trim();
        if (result.startsWith("\"") && result.endsWith("\"") && result.length() >= 2) {
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }
}
