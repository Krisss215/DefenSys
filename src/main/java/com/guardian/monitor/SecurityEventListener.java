// SecurityEventListener.java
package com.guardian.monitor;

public interface SecurityEventListener {
    void onNetworkChanged(boolean online, int activeConnections);
    void onUsbActivity(int totalEvents, int suspiciousEvents);
    void onStatsUpdated(int totalEvents, int blocked, int safeSessions, double[] trend);
}
