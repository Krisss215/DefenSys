package com.guardian.monitor;

import com.guardian.logging.SecureLogManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Periodically checks if the machine has internet connectivity.
 * On every transition "offline -> online" it logs an event
 * and notifies VirtualServerClient.
 */
public class InternetConnectivityWatcher implements Runnable {

    private static final long CHECK_INTERVAL_MS = 5000L;

    private volatile boolean wasOnline = false;

    @Override
    public void run() {
        SecureLogManager.getInstance().logEvent(
                "NET",
                "InternetConnectivityWatcher",
                "Internet connectivity watcher started."
        );

        while (!Thread.currentThread().isInterrupted()) {
            try {
                boolean online = isOnline();
                if (online && !wasOnline) {
                    SecureLogManager.getInstance().logEvent(
                            "INTERNET",
                            "InternetConnectivityWatcher",
                            "Host just connected to the internet."
                    );
                    try {
                        VirtualServerClient.notifyOnline();
                    } catch (Exception e) {
                        SecureLogManager.getInstance().logEvent(
                                "INTERNET",
                                "InternetConnectivityWatcher",
                                "Failed to notify virtual server: " + e.getMessage()
                        );
                    }
                }
                wasOnline = online;
                Thread.sleep(CHECK_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                SecureLogManager.getInstance().logEvent(
                        "INTERNET",
                        "InternetConnectivityWatcher",
                        "Error while checking internet connectivity: " + e.getMessage()
                );
            }
        }
    }

    private boolean isOnline() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
