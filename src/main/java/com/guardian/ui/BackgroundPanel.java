// BackgroundPanel.java
package com.guardian.ui;
import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {

    public BackgroundPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        GradientPaint gp = new GradientPaint(
                0, 0, AppTheme.getBackgroundGradientStart(),
                w, h, AppTheme.getBackgroundGradientEnd()
        );
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        g2.dispose();
        super.paintComponent(g);
    }
}
