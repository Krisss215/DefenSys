package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends GlassPanel {

    private final JLabel statusLabel;
    private final JLabel usbLabel;
    private final JLabel netLabel;
    private final JLabel title;

    public StatusPanel() {
        super(new BorderLayout(12, 6));

        GlassCard card = new GlassCard(true);
        add(card, BorderLayout.CENTER);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        title = new JLabel("USB Ninja – Agent status");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(AppTheme.getTitleColor());

        statusLabel = new JLabel("Agent stopped");
        statusLabel.setForeground(AppTheme.getSubtitleColor());

        usbLabel = new JLabel("USB: no events");
        usbLabel.setForeground(AppTheme.getSubtitleColor());

        netLabel = new JLabel("Network: no events");
        netLabel.setForeground(AppTheme.getSubtitleColor());

        content.add(title);
        content.add(Box.createVerticalStrut(6));
        content.add(statusLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(usbLabel);
        content.add(Box.createVerticalStrut(2));
        content.add(netLabel);

        card.add(card.createAccentColumn(), BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);
    }

    public void setRunning(boolean running) {
        statusLabel.setText(running ? "Agent running" : "Agent stopped");
    }

    public void setUsbStatus(String text) {
        usbLabel.setText("USB: " + text);
    }

    public void setNetStatus(String text) {
        netLabel.setText("Network: " + text);
    }

    public void setNetworkOnline(boolean online, int activeConnections) {
        String state = online ? "online" : "offline";
        setNetStatus(state + " (" + activeConnections + " connections)");
    }

    public void setUsbStats(int detected, int blocked) {
        setUsbStatus("devices: " + detected + ", blocked: " + blocked);
    }

    public void refreshColors() {
        title.setForeground(AppTheme.getTitleColor());
        statusLabel.setForeground(AppTheme.getSubtitleColor());
        usbLabel.setForeground(AppTheme.getSubtitleColor());
        netLabel.setForeground(AppTheme.getSubtitleColor());
    }
}
