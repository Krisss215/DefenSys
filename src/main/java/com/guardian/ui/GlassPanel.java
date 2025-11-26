package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Generic "glass" card used across the UI.
 * Rounded corners, soft shadow and semi-transparent background.
 *
 * Automatically switches between dark/light glass depending
 * on parent background brightness.
 */
public class GlassPanel extends JPanel {

    public GlassPanel() {
        this(null);
    }

    public GlassPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        int arc = 24;
        int shadowSize = 8;

        int x = shadowSize;
        int y = shadowSize;
        int w = getWidth() - shadowSize * 2;
        int h = getHeight() - shadowSize * 2;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Detect theme from parent background brightness
        boolean darkTheme = true;
        Container parent = getParent();
        if (parent != null) {
            Color bg = parent.getBackground();
            int lum = (bg.getRed() + bg.getGreen() + bg.getBlue()) / 3;
            // if background is relatively bright -> light theme
            darkTheme = lum < 160;
        }

        if (darkTheme) {
            // ===== Dark glass =====
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(x, y + 2, w, h, arc + 8, arc + 8);

            Color glass = new Color(20, 24, 40, 190);
            g2.setPaint(new GradientPaint(
                    0, y,
                    new Color(glass.getRed(), glass.getGreen(), glass.getBlue(), 220),
                    0, y + h,
                    new Color(glass.getRed(), glass.getGreen(), glass.getBlue(), 160)
            ));
            g2.fillRoundRect(x, y, w, h, arc, arc);

            g2.setColor(new Color(255, 255, 255, 70));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x, y, w, h, arc, arc);
        } else {
            // ===== Light glass =====
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(x, y + 2, w, h, arc + 8, arc + 8);

            Color top = new Color(255, 255, 255, 235);
            Color bottom = new Color(245, 245, 245, 215);
            g2.setPaint(new GradientPaint(
                    0, y,
                    top,
                    0, y + h,
                    bottom
            ));
            g2.fillRoundRect(x, y, w, h, arc, arc);

            g2.setColor(new Color(180, 180, 180, 120));
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawRoundRect(x, y, w, h, arc, arc);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
