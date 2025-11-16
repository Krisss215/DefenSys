package com.guardian.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GlassRow extends JPanel {
    private static final float ARC=16f;
    private static final int INSET=6;
    private static final TexturePaint NOISE=makeNoise();

    public GlassRow(){
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        AppTheme.addThemeListener(this::repaint);
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w=getWidth(), h=getHeight();
        float x=INSET, y=INSET, cw=w-INSET*2f, ch=h-INSET*2f;

        g2.setColor(AppTheme.GLASS_SHADOW);
        g2.fill(new RoundRectangle2D.Float(x, y+2, cw, ch, ARC+8, ARC+8));

        GradientPaint gp = new GradientPaint(0,(int)y, AppTheme.GLASS_TOP, 0,(int)(y+ch), AppTheme.GLASS_BOTTOM);
        Shape card=new RoundRectangle2D.Float(x,y,cw,ch,ARC,ARC);
        g2.setPaint(gp); g2.fill(card);

        g2.setPaint(AppTheme.GLASS_HIGHLIGHT);
        g2.fill(new RoundRectangle2D.Float(x+6,y+6,cw-12,Math.max(8,ch*0.28f),ARC,ARC));

        Composite old=g2.getComposite();
        g2.setComposite(AlphaComposite.SrcOver.derive(AppTheme.DARK ? 0.045f : 0.03f));
        g2.setPaint(NOISE); g2.fill(card);
        g2.setComposite(old);

        g2.setColor(AppTheme.GLASS_STROKE);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(x+.5f,y+.5f,cw-1f,ch-1f,ARC,ARC));

        g2.dispose();
        super.paintComponent(g);
    }

    private static TexturePaint makeNoise(){
        int s=40;
        BufferedImage img=new BufferedImage(s,s,BufferedImage.TYPE_INT_ARGB);
        Random r=new Random(21);
        for(int yy=0;yy<s;yy++)
            for(int xx=0;xx<s;xx++){
                int a=10+r.nextInt(10);
                img.setRGB(xx,yy,(a<<24)|0xFFFFFF);
            }
        return new TexturePaint(img,new Rectangle(0,0,s,s));
    }
}
