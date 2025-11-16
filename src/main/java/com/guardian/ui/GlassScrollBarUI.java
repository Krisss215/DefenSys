// GlassScrollBarUI.java
package com.guardian.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class GlassScrollBarUI extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(255, 255, 255, 80);
        trackColor = new Color(0, 0, 0, 0);
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(8, 32);
    }

    @Override
    protected Dimension getMaximumThumbSize() {
        return new Dimension(8, Integer.MAX_VALUE);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        b.setOpaque(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        return b;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if (!scrollbar.isEnabled() || r.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Color base = AppTheme.accentSoft();
        g2.setPaint(new GradientPaint(
                r.x, r.y, base,
                r.x, r.y + r.height, AppTheme.accent()
        ));
        int arc = 16;
        g2.fillRoundRect(r.x, r.y, r.width, r.height, arc, arc);

        g2.dispose();
    }
}
