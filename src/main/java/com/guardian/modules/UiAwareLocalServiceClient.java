package com.guardian.modules;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Обёртка вокруг LocalServiceClient, которая:
 *  - пересылает события "настоящему" клиенту (файл/HTTP)
 *  - параллельно обновляет UI через AgentStatusListener.
 */
public class UiAwareLocalServiceClient implements LocalServiceClient {

    private final LocalServiceClient delegate;
    private final AgentStatusListener listener;

    private final Set<String> activeUsbDevices = ConcurrentHashMap.newKeySet();
    private final Set<String> seenNetworkConnections = ConcurrentHashMap.newKeySet();

    public UiAwareLocalServiceClient(LocalServiceClient delegate, AgentStatusListener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public void sendEvent(SecurityEvent event) {
        handleForUi(event);
        delegate.sendEvent(event);
    }

    private void handleForUi(SecurityEvent event) {
        if (listener == null) {
            return;
        }
        switch (event.getType()) {
            case USB_CONNECTED: {
                String key = extractUsbKey(event.getDetails());
                if (key != null) {
                    activeUsbDevices.add(key);
                    listener.onUsbStatsChanged(activeUsbDevices.size(), 0);
                }
                break;
            }
            case USB_DISCONNECTED: {
                String key = extractUsbKey(event.getDetails());
                if (key != null) {
                    activeUsbDevices.remove(key);
                    listener.onUsbStatsChanged(activeUsbDevices.size(), 0);
                }
                break;
            }
            case NETWORK_NEW_CONNECTION: {
                Map<String, String> d = event.getDetails();
                if (d != null) {
                    String conn = d.get("connection");
                    if (conn != null && seenNetworkConnections.add(conn)) {
                        listener.onNetworkStatsChanged(seenNetworkConnections.size());
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private String extractUsbKey(Map<String, String> details) {
        if (details == null) {
            return null;
        }
        if (details.containsKey("deviceId")) {
            return "ID:" + details.get("deviceId");
        }
        if (details.containsKey("deviceName")) {
            return "NAME:" + details.get("deviceName");
        }
        if (details.containsKey("raw")) {
            return "RAW:" + details.get("raw");
        }
        return null;
    }
}
