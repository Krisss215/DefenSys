// GlassScrollBarUI.java
package com.guardian.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class GlassScrollBarUI extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        trackColor = new Color(0, 0, 0, 0);
        thumbColor = new Color(140, 156, 220, 160);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createInvisibleButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createInvisibleButton();
    }

    private JButton createInvisibleButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setFocusable(false);
        b.setOpaque(false);
        return b;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // Ничего — прозрачный
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!c.isEnabled()) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(thumbColor);
        int arc = 10;
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, arc, arc);

        g2.dispose();
    }
}
