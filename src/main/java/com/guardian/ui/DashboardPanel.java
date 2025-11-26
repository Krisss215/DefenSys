package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main dashboard panel:
 *  - status card
 *  - simple chart
 *  - event history (HistoryPanel)
 *
 * Supports simple Dark/Light theme controlled by AgentUIModern.
 */
public class DashboardPanel extends JPanel {

    // old theme object is kept for backward compatibility, but not used
    private AppTheme appTheme;

    private final StatusPanel statusPanel;
    private final HistoryPanel historyPanel;
    private final SimpleChart simpleChart;

    private boolean darkTheme = true;

    public DashboardPanel() {
        this(null);
    }

    public DashboardPanel(AppTheme theme) {
        this.appTheme = theme;

        setLayout(new BorderLayout());
        setOpaque(false);

        // Top part – status + chart
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        statusPanel = new StatusPanel();
        gbc.gridx = 0;
        gbc.weightx = 0.6;
        topPanel.add(statusPanel, gbc);

        simpleChart = new SimpleChart();
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        topPanel.add(simpleChart, gbc);

        // Bottom – event history
        historyPanel = new HistoryPanel();
        JPanel historyWrapper = new JPanel(new BorderLayout());
        historyWrapper.setOpaque(false);
        historyWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        historyWrapper.add(historyPanel, BorderLayout.CENTER);

        // Combine
        add(topPanel, BorderLayout.CENTER);
        add(historyWrapper, BorderLayout.SOUTH);

        applyTheme();
    }

    /**
     * Set theme object from external code (kept for backward compatibility).
     */
    public void setAppTheme(AppTheme theme) {
        this.appTheme = theme;
        refreshColors();
    }

    /**
     * Called from AgentUIModern when Dark/Light toggle is changed.
     */
    public void setDarkTheme(boolean dark) {
        this.darkTheme = dark;
        refreshColors();
    }

    /**
     * Old API that may be called from other parts when theme changed.
     */
    public void refreshColors() {
        applyTheme();
        historyPanel.refreshColors();
        revalidate();
        repaint();
    }

    private void applyTheme() {
        Color bg = darkTheme ? new Color(0x141625) : new Color(0xF0F2F5);
        Color fg = darkTheme ? Color.WHITE : Color.DARK_GRAY;

        setBackground(bg);

        statusPanel.setBackground(bg);
        statusPanel.setForeground(fg);

        simpleChart.setBackground(bg);
        simpleChart.setForeground(fg);

        historyPanel.setBackground(bg);
        historyPanel.setDarkTheme(darkTheme);
    }

    public HistoryPanel getHistoryPanel() {
        return historyPanel;
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }

    public SimpleChart getSimpleChart() {
        return simpleChart;
    }

    /**
     * Compatibility method for old code that calls updateStats(...).
     */
    public void updateStats(int usbCount, int netCount, int other, double[] values) {
        // Here you can later add real chart/status update logic.
        // For now, method exists only for backward compatibility.
    }
}
