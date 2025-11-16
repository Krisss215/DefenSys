// BackgroundPanel.java
package com.guardian.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class BackgroundPanel extends JPanel implements PropertyChangeListener {

    public BackgroundPanel() {
        setOpaque(false);
        AppTheme.addThemeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mode".equals(evt.getPropertyName())) {
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(AppTheme.backgroundGradient(w, h));
        g2.fillRect(0, 0, w, h);

        g2.setPaint(new RadialGradientPaint(
                new Point(w / 4, h / 4),
                Math.max(w, h) / 2f,
                new float[]{0f, 1f},
                new Color[]{AppTheme.accentSoft(), new Color(0, 0, 0, 0)}
        ));
        g2.fillRect(0, 0, w, h);

        g2.dispose();
    }
}
