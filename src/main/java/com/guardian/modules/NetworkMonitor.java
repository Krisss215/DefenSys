package com.guardian.modules;

import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkMonitor {
    public static boolean hasInternetConnection() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("1.1.1.1", 53), 900);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
