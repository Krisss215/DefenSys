// StatusPanel.java
package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {

    private final GlassCard cardNet =
            new GlassCard("Network", "Offline", "No active connections", AppTheme.WARN());
    private final GlassCard cardUsb =
            new GlassCard("USB", "0 events", "No suspicious devices", AppTheme.OK());

    public StatusPanel() {
        setOpaque(false);
        setLayout(new GridLayout(2, 1, 12, 12));

        add(cardNet);
        add(cardUsb);
    }

    public void setNetworkOnline(boolean online, int activeConnections) {
        if (online) {
            cardNet.setAccentColor(AppTheme.OK());
            cardNet.setAlertMode(false);
            cardNet.setValue(activeConnections + " connections");
            cardNet.setSubtitle("Firewall active, monitoring enabled");
        } else {
            cardNet.setAccentColor(AppTheme.WARN());
            cardNet.setAlertMode(true);
            cardNet.setValue("Offline");
            cardNet.setSubtitle("No active connections detected");
        }
    }

    public void setUsbStats(int totalEvents, int suspiciousEvents) {
        if (suspiciousEvents > 0) {
            cardUsb.setAccentColor(AppTheme.ERROR());
            cardUsb.setAlertMode(true);
            cardUsb.setValue(totalEvents + " / " + suspiciousEvents);
            cardUsb.setSubtitle("Suspicious USB activity detected");
        } else {
            cardUsb.setAccentColor(AppTheme.OK());
            cardUsb.setAlertMode(false);
            cardUsb.setValue(totalEvents + " devices");
            cardUsb.setSubtitle("All devices verified");
        }
    }
}
