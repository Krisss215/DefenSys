// ControlPanel.java
package com.guardian.ui;

import com.guardian.monitor.MonitorService;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ControlPanel extends GlassPanel implements PropertyChangeListener {

    private final JButton startButton = new JButton("Start agent");
    private final JButton stopButton  = new JButton("Stop agent");
    private final JLabel headerLabel = new JLabel("Controls");

    public ControlPanel(MonitorService service) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));

        headerLabel.setFont(AppTheme.titleFont(15f));
        headerLabel.setForeground(AppTheme.textPrimary()); // заголовок -> основной цвет
        add(headerLabel);

        stylePillButton(startButton);
        stylePillButton(stopButton);

        startButton.setBackground(AppTheme.OK());
        stopButton.setBackground(AppTheme.ERROR());

        startButton.addActionListener(e -> {
            service.startAgent();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        });

        stopButton.addActionListener(e -> {
            service.stopAgent();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        });

        stopButton.setEnabled(false);

        add(startButton);
        add(stopButton);

        AppTheme.addThemeListener(this);
    }

    private void stylePillButton(JButton b) {
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setForeground(Color.WHITE);
        b.setFont(AppTheme.bodyFont(12f));
        b.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mode".equals(evt.getPropertyName())) {
            headerLabel.setForeground(AppTheme.textPrimary());
            repaint();
        }
    }
}
