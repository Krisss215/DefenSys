package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

public class SimpleChart extends GlassPanel {

    private double[] values = new double[0];

    private final JPanel chartPanel;

    public SimpleChart() {
        super(new BorderLayout());

        GlassCard card = new GlassCard(false);
        add(card, BorderLayout.CENTER);

        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintChart((Graphics2D) g);
            }
        };
        chartPanel.setOpaque(false);

        card.add(chartPanel, BorderLayout.CENTER);
    }

    public void setData(double[] data) {
        if (data == null) {
            this.values = new double[0];
        } else {
            this.values = data.clone();
        }
        chartPanel.repaint();
    }

    public void refreshColors() {
        chartPanel.repaint();
    }

    private void paintChart(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = chartPanel.getWidth();
        int h = chartPanel.getHeight();

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 13f));
        g2.setColor(AppTheme.getSubtitleColor());

        if (values == null || values.length == 0) {
            String text = "No data yet";
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);
            return;
        }

        double max = 0.0;
        for (double v : values) {
            if (v > max) max = v;
        }
        if (max <= 0) max = 1.0;

        int marginX = 24;
        int marginY = 24;
        int chartW = w - marginX * 2;
        int chartH = h - marginY * 2;

        if (chartW <= 0 || chartH <= 0) return;

        // axis
        g2.setColor(new Color(180, 190, 230, 180));
        g2.drawLine(marginX, h - marginY, marginX + chartW, h - marginY);
        g2.drawLine(marginX, h - marginY, marginX, marginY);

        int n = values.length;
        int barSpace = chartW / Math.max(n, 1);
        int barWidth = Math.max(6, (int) (barSpace * 0.5));

        for (int i = 0; i < n; i++) {
            double v = values[i];
            int barHeight = (int) (chartH * (v / max));
            int x = marginX + i * barSpace + (barSpace - barWidth) / 2;
            int y = h - marginY - barHeight;

            GradientPaint gp = new GradientPaint(
                    x, y,
                    AppTheme.getAccentStart(),
                    x, y + barHeight,
                    AppTheme.getAccentEnd()
            );
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);
        }
    }
}
