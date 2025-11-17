package com.guardian.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThemeSwitch extends JComponent {

    // true = DARK (thumb on the right), false = LIGHT (thumb on the left)
    private boolean dark = true;
    private Runnable onToggle;

    public ThemeSwitch() {
        setPreferredSize(new Dimension(64, 30));
        setMinimumSize(new Dimension(64, 30));
        setMaximumSize(new Dimension(64, 30));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggle();
            }
        });
    }

    public void setMode(ThemeMode mode) {
        dark = (mode == ThemeMode.DARK);
        repaint();
    }

    public ThemeMode getMode() {
        return dark ? ThemeMode.DARK : ThemeMode.LIGHT;
    }

    public void setOnToggle(Runnable onToggle) {
        this.onToggle = onToggle;
    }

    private void toggle() {
        dark = !dark;
        if (onToggle != null) {
            onToggle.run();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = h;

        // track
        g2.setColor(AppTheme.getToggleTrack());
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        int padding = 3;
        int thumbSize = h - padding * 2;

        // DARK → thumb on the right, LIGHT → thumb on the left
        int xThumb = dark ? (w - padding - thumbSize) : padding;

        // thumb
        g2.setColor(AppTheme.getToggleThumb());
        g2.fillOval(xThumb, padding, thumbSize, thumbSize);

        // icon (sun for light, moon for dark)
        int iconSize = thumbSize - 8;
        int iconX = xThumb + (thumbSize - iconSize) / 2;
        int iconY = padding + (thumbSize - iconSize) / 2;

        if (!dark) {
            // LIGHT: draw sun
            g2.setColor(new Color(255, 204, 0));
            g2.fillOval(iconX, iconY, iconSize, iconSize);
        } else {
            // DARK: draw moon
            g2.setColor(new Color(185, 195, 255));
            g2.fillOval(iconX, iconY, iconSize, iconSize);
            g2.setColor(AppTheme.getToggleThumb());
            g2.fillOval(iconX + iconSize / 3, iconY, iconSize, iconSize);
        }

        g2.dispose();
    }
}
