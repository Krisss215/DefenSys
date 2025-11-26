package com.guardian.monitor;

import com.guardian.logging.SecureLogManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Monitors network adapters, Wi-Fi and Bluetooth using PowerShell.
 */
public class NetworkMonitorService implements Runnable {

    private static final long POLL_INTERVAL_MS = 5000L;

    private final Set<String> knownNetAdapters = new HashSet<>();
    private final Set<String> knownBluetooth = new HashSet<>();

    @Override
    public void run() {
        SecureLogManager.getInstance().logEvent(
                "NET",
                "NetworkMonitorService",
                "Network monitor started."
        );

        while (!Thread.currentThread().isInterrupted()) {
            try {
                checkNetworkAdapters();
                checkBluetoothDevices();
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                SecureLogManager.getInstance().logEvent(
                        "NET",
                        "NetworkMonitorService",
                        "Error while polling network devices: " + e.getMessage()
                );
            }
        }
    }

    private void checkNetworkAdapters() throws IOException {
        String csv = PowerShellHelper.run(
                "$nics = Get-NetAdapter | Where-Object { $_.Status -eq 'Up' }; " +
                        "$nics | Select-Object Name, InterfaceDescription | ConvertTo-Csv -NoTypeInformation"
        );

        Set<String> current = new HashSet<>();
        String[] lines = csv.split("\\r?\\n");

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",", 2);
            if (parts.length < 2) continue;

            String name = unquote(parts[0]);
            String desc = unquote(parts[1]);
            String id = name + "|" + desc;
            current.add(id);
        }

        Set<String> newAdapters = new HashSet<>(current);
        newAdapters.removeAll(knownNetAdapters);

        Set<String> removedAdapters = new HashSet<>(knownNetAdapters);
        removedAdapters.removeAll(current);

        for (String id : newAdapters) {
            String[] parts = id.split("\\|", 2);
            SecureLogManager.getInstance().logEvent(
                    "NET-ADAPTER",
                    "NetworkMonitorService",
                    "Network adapter connected: '" + parts[0] + "', '" + parts[1] + "'"
            );
        }

        for (String id : removedAdapters) {
            String[] parts = id.split("\\|", 2);
            SecureLogManager.getInstance().logEvent(
                    "NET-ADAPTER",
                    "NetworkMonitorService",
                    "Network adapter disconnected: '" + parts[0] + "', '" + parts[1] + "'"
            );
        }

        knownNetAdapters.clear();
        knownNetAdapters.addAll(current);
    }

    private void checkBluetoothDevices() throws IOException {
        String csv = PowerShellHelper.run(
                "$bt = Get-PnpDevice | " +
                        "Where-Object { $_.Class -eq 'Bluetooth' -and $_.Status -eq 'OK' }; " +
                        "$bt | Select-Object FriendlyName | ConvertTo-Csv -NoTypeInformation"
        );

        Set<String> current = new HashSet<>();
        String[] lines = csv.split("\\r?\\n");

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String name = unquote(line.split(",", 2)[0]);
            current.add(name);
        }

        Set<String> newDevices = new HashSet<>(current);
        newDevices.removeAll(knownBluetooth);

        Set<String> removedDevices = new HashSet<>(knownBluetooth);
        removedDevices.removeAll(current);

        for (String name : newDevices) {
            SecureLogManager.getInstance().logEvent(
                    "BLUETOOTH",
                    "NetworkMonitorService",
                    "Bluetooth device connected: '" + name + "'"
            );
        }

        for (String name : removedDevices) {
            SecureLogManager.getInstance().logEvent(
                    "BLUETOOTH",
                    "NetworkMonitorService",
                    "Bluetooth device disconnected: '" + name + "'"
            );
        }

        knownBluetooth.clear();
        knownBluetooth.addAll(current);
    }

    private String unquote(String s) {
        String result = s.trim();
        if (result.startsWith("\"") && result.endsWith("\"") && result.length() >= 2) {
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }
}
