package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

public class ThemeToggleButton extends JToggleButton {

    private float t = 0f; // 0..1

    public ThemeToggleButton(boolean dark) {
        setSelected(dark);
        setFocusable(false);
        setBorder(BorderFactory.createEmptyBorder(6,8,6,8));
        setOpaque(false);
        Timer anim = new Timer(15, e -> {
            float target = isSelected() ? 1f : 0f;
            t += (target - t) * 0.25f;
            if (Math.abs(target - t) < 0.01f) t = target;
            repaint();
        });
        anim.start();
        AppTheme.addThemeListener(this::repaint);
    }

    @Override public Dimension getPreferredSize() { return new Dimension(86,32); }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w=getWidth(), h=getHeight();
        int arc=18;

        g2.setColor(AppTheme.GLASS_SHADOW);
        g2.fillRoundRect(0,0,w,h,arc+8,arc+8);
        g2.setPaint(new GradientPaint(0,0,AppTheme.GLASS_TOP,0,h,AppTheme.GLASS_BOTTOM));
        g2.fillRoundRect(0,0,w,h,arc,arc);
        g2.setColor(AppTheme.GLASS_STROKE);
        g2.drawRoundRect(0,0,w-1,h-1,arc,arc);

        int cx=h/2 + 4, cy=h/2, r=h/2-6;

        g2.setColor(new Color(255,255,255, AppTheme.DARK ? 20 : 50));
        g2.fillOval(cx-r-3, cy-r-3, (r*2)+6, (r*2)+6);

        Color ic = AppTheme.DARK ? new Color(245,245,255) : new Color(40,40,55);
        g2.setColor(ic);

        if (t < 0.5f) { // Солнце
            g2.fillOval(cx-r, cy-r, r*2, r*2);
            g2.setColor(new Color(0,0,0,(int)(70*(0.5f-t)*2)));
            g2.fillOval(cx-r/3, cy-r/3, r*2, r*2);
        } else { // Луна
            g2.fillOval(cx-r, cy-r, r*2, r*2);
            g2.setComposite(AlphaComposite.Clear);
            g2.fillOval(cx-r/3, cy-r, r*2, r*2);
            g2.setComposite(AlphaComposite.SrcOver);
        }

        g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
        String txt = isSelected() ? "Dark" : "Light";
        int tx = h, ty = (h+g2.getFontMetrics().getAscent())/2 - 2;

        g2.setColor(new Color(0,0,0, AppTheme.DARK ? 120 : 60));
        g2.drawString(txt, tx+1, ty+1);
        g2.setColor(AppTheme.TEXT_PRIMARY);
        g2.drawString(txt, tx, ty);

        g2.dispose();
    }
}
