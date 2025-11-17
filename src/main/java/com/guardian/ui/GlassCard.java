// GlassCard.java
package com.guardian.ui;
import javax.swing.*;
import java.awt.*;

public class GlassCard extends JPanel {

    private final boolean showAccent;

    public GlassCard(boolean showAccent) {
        this.showAccent = showAccent;
        setOpaque(false);
        setLayout(new BorderLayout(16, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 26;
        int w = getWidth();
        int h = getHeight();

        // Стеклянный фон
        g2.setColor(AppTheme.getGlassBackground());
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // Обводка
        g2.setColor(AppTheme.getGlassBorder());
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }

    public Component createAccentColumn() {
        if (!showAccent) return Box.createHorizontalStrut(0);

        JPanel accent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                        0, 0, AppTheme.getAccentStart(),
                        0, h, AppTheme.getAccentEnd()
                );
                g2.setPaint(gp);
                // Полностью скруглённый столбик — НИЧЕГО не обрезается
                g2.fillRoundRect(0, 0, w, h, w, w);

                g2.dispose();
            }
        };
        accent.setOpaque(false);
        accent.setPreferredSize(new Dimension(20, 10));

        return accent;
    }
}
