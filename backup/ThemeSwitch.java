package com.guardian.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeSwitch extends JComponent {
    private boolean dark = AppTheme.isDark();
    private final List<java.util.function.Consumer<Boolean>> listeners = new ArrayList<>();

    public ThemeSwitch() {
        setPreferredSize(new Dimension(96, 34));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                dark = !dark;
                listeners.forEach(l -> l.accept(dark));
                repaint();
            }
        });
    }

    public void addChangeListener(java.util.function.Consumer<Boolean> l) { listeners.add(l); }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        g2.setColor(AppTheme.isDark() ? new Color(40, 46, 56) : new Color(225, 233, 245));
        g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);
        g2.setColor(AppTheme.tint());
        g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);

        int knob = h - 10;
        int x = dark ? w - knob - 5 : 5;
        g2.setColor(AppTheme.isDark() ? new Color(230, 230, 235) : new Color(35, 35, 40));
        g2.fillOval(x, 5, knob, knob);

        g2.setFont(getFont().deriveFont(Font.BOLD));
        String text = dark ? "Dark" : "Light";
        g2.setColor(AppTheme.isDark() ? Color.WHITE : Color.BLACK);
        g2.drawString(text,  (dark ? 10 : 42),  h/2 + 5);
        g2.dispose();
    }
}
