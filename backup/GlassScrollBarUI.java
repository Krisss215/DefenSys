package com.guardian.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class GlassScrollBarUI extends BasicScrollBarUI {

    public static void install(JScrollPane sp){
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder(6,12,10,12));
        sp.getVerticalScrollBar().setOpaque(false);
        sp.getHorizontalScrollBar().setOpaque(false);
        sp.getVerticalScrollBar().setUI(new GlassScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new GlassScrollBarUI());
    }

    @Override
    protected void configureScrollBarColors() {
        trackColor = AppTheme.DARK ? new Color(120,170,220,55) : new Color(120,170,220,35);
        thumbDarkShadowColor = thumbLightShadowColor = thumbHighlightColor = new Color(0,0,0,0);
    }

    @Override protected JButton createDecreaseButton(int orientation) { return arrowBtn(true); }
    @Override protected JButton createIncreaseButton(int orientation) { return arrowBtn(false); }

    private JButton arrowBtn(boolean up) {
        JButton b = new JButton(){
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w=getWidth(), h=getHeight(), arc=10;
                g2.setColor(AppTheme.GLASS_SHADOW);
                g2.fillRoundRect(1,1,w-2,h-2,arc+6,arc+6);
                g2.setPaint(new GradientPaint(0,0,AppTheme.GLASS_TOP,0,h,AppTheme.GLASS_BOTTOM));
                g2.fillRoundRect(1,1,w-2,h-2,arc,arc);
                g2.setColor(AppTheme.GLASS_STROKE);
                g2.drawRoundRect(1,1,w-2,h-2,arc,arc);
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(AppTheme.TEXT_PRIMARY);
                int cx=w/2, cy=h/2, s=Math.min(w,h)/3;
                if(up){ g2.drawLine(cx-s, cy+2, cx, cy-s); g2.drawLine(cx+s, cy+2, cx, cy-s); }
                else  { g2.drawLine(cx-s, cy-2, cx, cy+s); g2.drawLine(cx+s, cy-2, cx, cy+s); }
                g2.dispose();
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        b.setFocusable(false);
        return b;
    }

    @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc=10;
        g2.setColor(trackColor);
        g2.fillRoundRect(r.x+5, r.y+2, r.width-10, r.height-4, arc, arc);
        g2.dispose();
    }

    @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc=12;

        g2.setColor(AppTheme.GLASS_SHADOW);
        g2.fillRoundRect(r.x+1, r.y+3, r.width-2, r.height-6, arc+8, arc+8);

        Color top = AppTheme.DARK ? new Color(210,235,255,220) : AppTheme.GLASS_TOP;
        Color bot = AppTheme.DARK ? new Color(170,210,255,200) : AppTheme.GLASS_BOTTOM;
        g2.setPaint(new GradientPaint(0, r.y, top, 0, r.y+r.height, bot));
        g2.fillRoundRect(r.x+1, r.y+2, r.width-2, r.height-6, arc, arc);

        g2.setColor(AppTheme.GLASS_STROKE);
        g2.drawRoundRect(r.x+1, r.y+2, r.width-2, r.height-6, arc, arc);
        g2.dispose();
    }

    @Override public Dimension getPreferredSize(JComponent c) {
        return (scrollbar.getOrientation()==Adjustable.VERTICAL)
                ? new Dimension(14, 40) : new Dimension(40, 14);
    }
}
