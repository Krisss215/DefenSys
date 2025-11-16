// GlassPanel.java
package com.guardian.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GlassPanel extends JPanel implements PropertyChangeListener {

    private int cornerRadius = 24;

    public GlassPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        AppTheme.addThemeListener(this);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mode".equals(evt.getPropertyName())) {
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(4, 6, w - 8, h - 10, cornerRadius + 6, cornerRadius + 6);

        g2.setColor(AppTheme.glassBackground());
        g2.fillRoundRect(0, 0, w - 8, h - 8, cornerRadius, cornerRadius);

        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(AppTheme.glassBorder());
        g2.drawRoundRect(0, 0, w - 8, h - 8, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
