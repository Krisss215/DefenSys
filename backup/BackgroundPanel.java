package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    public BackgroundPanel(){ setOpaque(true); }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g.create();
        g2.setPaint(new GradientPaint(0,0, AppTheme.BG_TOP, 0,getHeight(), AppTheme.BG_BOTTOM));
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.dispose();
    }
}
