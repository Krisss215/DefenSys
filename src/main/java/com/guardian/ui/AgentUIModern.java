package com.guardian.ui;

import com.guardian.monitor.MonitorService;

import javax.swing.*;
import java.awt.*;

public class AgentUIModern extends JFrame {

    private final BackgroundPanel backgroundPanel;
    private final DashboardPanel dashboardPanel;
    private final ControlPanel controlPanel;
    private final MonitorService monitorService;

    public AgentUIModern() {
        super("USB Ninja – Windows Security Agent");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 580));

        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout(0, 16));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(18, 24, 24, 24));

        dashboardPanel = new DashboardPanel();
        monitorService = new MonitorService(dashboardPanel);

        controlPanel = new ControlPanel(new ControlPanel.ControlListener() {
            @Override
            public void onStart() {
                monitorService.startAgent();
            }

            @Override
            public void onStop() {
                monitorService.stopAgent();
            }

            @Override
            public void onThemeChanged(ThemeMode mode) {
                refreshTheme();
                dashboardPanel.getHistoryPanel().addEvent(
                        "now", "UI", "Theme changed to " +
                                (mode == ThemeMode.DARK ? "dark" : "light"), false);
            }
        });

        backgroundPanel.add(controlPanel, BorderLayout.NORTH);
        backgroundPanel.add(dashboardPanel, BorderLayout.CENTER);

        setContentPane(backgroundPanel);
        refreshTheme();
    }

    private void refreshTheme() {
        backgroundPanel.setBackground(AppTheme.getBackground());
        dashboardPanel.refreshColors();
        controlPanel.refreshColors();
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }
}
