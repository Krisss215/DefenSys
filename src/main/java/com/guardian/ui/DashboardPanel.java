package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends GlassPanel {

    private final StatusPanel statusPanel;
    private final SimpleChart chart;
    private final HistoryPanel historyPanel;

    public DashboardPanel() {
        super(new BorderLayout(0, 16));

        statusPanel = new StatusPanel();
        chart = new SimpleChart();
        historyPanel = new HistoryPanel();

        GlassRow topRow = new GlassRow();
        topRow.add(statusPanel);
        topRow.add(chart);

        add(topRow, BorderLayout.CENTER);
        add(Box.createVerticalStrut(4), BorderLayout.NORTH);
        add(historyPanel, BorderLayout.SOUTH);
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }

    public HistoryPanel getHistoryPanel() {
        return historyPanel;
    }

    public void updateStats(int usbEvents, int netEvents, int alerts, double[] timeline) {
        chart.setData(timeline);
    }

    public void refreshColors() {
        statusPanel.refreshColors();
        historyPanel.refreshColors();
        chart.refreshColors();
    }
}

