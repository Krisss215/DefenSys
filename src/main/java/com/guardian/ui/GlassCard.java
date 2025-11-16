// GlassCard.java
package com.guardian.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GlassCard extends GlassPanel implements PropertyChangeListener {

    private final JLabel titleLabel = new JLabel();
    private final JLabel valueLabel = new JLabel();
    private final JLabel subtitleLabel = new JLabel();
    private Color accentColor = AppTheme.accent();

    private boolean alertMode = false;
    private float pulsePhase = 0f;
    private final Timer pulseTimer;

    public GlassCard(String title, String value, Color accent) {
        this(title, value, "", accent);
    }

    public GlassCard(String title, String value, String subtitle, Color accent) {
        this.accentColor = accent;
        setLayout(new BorderLayout(8, 8));

        titleLabel.setText(title);
        titleLabel.setFont(AppTheme.bodyFont(12f));   // было 13f
        titleLabel.setForeground(AppTheme.textSecondary());

        valueLabel.setText(value);
        valueLabel.setFont(AppTheme.titleFont(18f));  // было 22f
        valueLabel.setForeground(AppTheme.textPrimary());

        subtitleLabel.setText(subtitle);
        subtitleLabel.setFont(AppTheme.bodyFont(10f)); // было 11f
        subtitleLabel.setForeground(AppTheme.textSecondary());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
        add(subtitleLabel, BorderLayout.SOUTH);

        pulseTimer = new Timer(16, e -> {
            pulsePhase += 0.02f;
            if (pulsePhase > 1f) pulsePhase -= 1f;
            repaint();
        });

        AppTheme.addThemeListener(this);
    }

    public void setAccentColor(Color c) {
        this.accentColor = c;
        repaint();
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    public void setAlertMode(boolean alert) {
        if (this.alertMode == alert) return;
        this.alertMode = alert;
        if (alertMode) {
            pulsePhase = 0f;
            pulseTimer.start();
        } else {
            pulseTimer.stop();
            repaint();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mode".equals(evt.getPropertyName())) {
            titleLabel.setForeground(AppTheme.textSecondary());
            valueLabel.setForeground(AppTheme.textPrimary());
            subtitleLabel.setForeground(AppTheme.textSecondary());
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int h = getHeight();

        Color base = new Color(
                accentColor.getRed(),
                accentColor.getGreen(),
                accentColor.getBlue(),
                200
        );

        if (alertMode) {
            float s = (float) ((Math.sin(pulsePhase * Math.PI * 2) + 1) / 2.0);
            int alpha = 120 + (int) (80 * s);
            base = new Color(
                    accentColor.getRed(),
                    accentColor.getGreen(),
                    accentColor.getBlue(),
                    alpha
            );
        }

        g2.setColor(base);
        g2.fillRoundRect(6, 10, 4, h - 26, 8, 8);

        g2.dispose();
    }
}
