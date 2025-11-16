// ThemeSwitch.java
package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Переключатель темы в виде ползунка "солнце ↔ луна".
 */
public class ThemeSwitch extends JToggleButton {

    public ThemeSwitch() {
        setModel(new ToggleButtonModel());
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Toggle light / dark theme");

        setSelected(AppTheme.getMode() == ThemeMode.DARK);

        addActionListener(e -> {
            ThemeMode newMode = isSelected() ? ThemeMode.DARK : ThemeMode.LIGHT;
            AppTheme.setMode(newMode);
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(56, 28);
    }

    @Override
    protected void paintComponent(Graphics g) {
        boolean dark = isSelected();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = h;

        // фон трека
        Color trackLight = new Color(240, 244, 255, 200);
        Color trackDark  = new Color(30, 36, 60, 220);
        g2.setColor(dark ? trackDark : trackLight);
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // граница
        g2.setColor(AppTheme.glassBorder());
        g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // положение бегунка: слева (light / sun) или справа (dark / moon)
        int thumbSize = h - 6;
        int x = dark ? (w - thumbSize - 3) : 3;
        int y = 3;

        // бегунок
        g2.setColor(Color.WHITE);
        g2.fillOval(x, y, thumbSize, thumbSize);
        g2.setColor(new Color(0, 0, 0, 40));
        g2.drawOval(x, y, thumbSize, thumbSize);

        // иконка солнце / луна
        g2.setClip(x, y, thumbSize, thumbSize);
        g2.translate(x, y);

        if (!dark) {
            // солнце
            g2.setColor(new Color(255, 180, 0));
            int r = thumbSize / 2 - 4;
            int cx = thumbSize / 2;
            int cy = thumbSize / 2;
            g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);

            g2.setStroke(new BasicStroke(1.2f));
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4;
                int x1 = cx + (int) (Math.cos(angle) * (r + 2));
                int y1 = cy + (int) (Math.sin(angle) * (r + 2));
                int x2 = cx + (int) (Math.cos(angle) * (r + 6));
                int y2 = cy + (int) (Math.sin(angle) * (r + 6));
                g2.drawLine(x1, y1, x2, y2);
            }
        } else {
            // луна
            int r = thumbSize / 2 - 2;
            int cx = thumbSize / 2;
            int cy = thumbSize / 2;
            g2.setColor(new Color(245, 245, 255));
            g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);

            g2.setColor(new Color(200, 205, 230));
            g2.fillOval(cx - r / 2, cy - r, 2 * r, 2 * r);
        }

        g2.dispose();
    }
}
