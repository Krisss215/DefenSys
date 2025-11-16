// SimpleChart.java
package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

public class SimpleChart extends JPanel {

    private double[] values = new double[]{2, 4, 1, 6, 3, 7, 5, 9};

    public SimpleChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(320, 160));
    }

    public void setValues(double[] values) {
        this.values = values != null ? values : new double[0];
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (values == null || values.length == 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int padding = 16;

        g2.setColor(new Color(255, 255, 255, 40));
        g2.drawLine(padding, h - padding, w - padding, h - padding);
        g2.drawLine(padding, padding, padding, h - padding);

        double min = values[0];
        double max = values[0];
        for (double v : values) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        if (max == min) max = min + 1;

        double xStep = (double) (w - 2 * padding) / (values.length - 1);
        double scale = (double) (h - 2 * padding) / (max - min);

        int[] xs = new int[values.length + 2];
        int[] ys = new int[values.length + 2];

        for (int i = 0; i < values.length; i++) {
            xs[i] = (int) (padding + xStep * i);
            ys[i] = (int) (h - padding - (values[i] - min) * scale);
        }
        xs[values.length] = padding + (int) (xStep * (values.length - 1));
        ys[values.length] = h - padding;
        xs[values.length + 1] = padding;
        ys[values.length + 1] = h - padding;

        g2.setPaint(new GradientPaint(
                0, padding, AppTheme.accentSoft(),
                0, h, new Color(0, 0, 0, 0)
        ));
        g2.fillPolygon(xs, ys, xs.length);

        g2.setStroke(new BasicStroke(2.0f));
        g2.setColor(AppTheme.accent());
        for (int i = 0; i < values.length - 1; i++) {
            g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
        }

        g2.dispose();
    }
}
