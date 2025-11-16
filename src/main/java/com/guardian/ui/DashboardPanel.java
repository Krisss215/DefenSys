// DashboardPanel.java
package com.guardian.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DashboardPanel extends JPanel implements PropertyChangeListener {

    private final SimpleChart chart = new SimpleChart();
    private final GlassCard cardEvents =
            new GlassCard("Events (24h)", "0", AppTheme.accent());
    private final GlassCard cardBlocked =
            new GlassCard("Blocked threats", "0", AppTheme.ERROR());
    private final GlassCard cardSafe =
            new GlassCard("Safe sessions", "0", AppTheme.OK());

    private final JLabel title;
    private final JLabel subtitle;

    public DashboardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 16));

        title = new JLabel("Security Overview");
        title.setFont(AppTheme.titleFont(19f));
        title.setForeground(AppTheme.textPrimary());

        subtitle = new JLabel("Windows agent · USB · Network");
        subtitle.setFont(AppTheme.bodyFont(11f));
        subtitle.setForeground(AppTheme.textSecondary());

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(Box.createVerticalStrut(2));
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        GlassPanel centerGlass = new GlassPanel();
        centerGlass.setLayout(new BorderLayout());
        centerGlass.add(chart, BorderLayout.CENTER);
        add(centerGlass, BorderLayout.CENTER);

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 12, 12));
        statsRow.setOpaque(false);
        statsRow.add(cardEvents);
        statsRow.add(cardBlocked);
        statsRow.add(cardSafe);

        add(statsRow, BorderLayout.SOUTH);

        AppTheme.addThemeListener(this);
    }

    public void updateStats(int totalEvents, int blocked, int safeSessions, double[] trend) {
        cardEvents.setValue(String.valueOf(totalEvents));
        cardBlocked.setValue(String.valueOf(blocked));
        cardSafe.setValue(String.valueOf(safeSessions));
        if (trend != null) {
            chart.setValues(trend);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mode".equals(evt.getPropertyName())) {
            title.setForeground(AppTheme.textPrimary());
            subtitle.setForeground(AppTheme.textSecondary());
            repaint();
        }
    }
}
