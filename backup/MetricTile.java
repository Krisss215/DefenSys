package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Small glass card with a title, value line, and a status dot.
 * Exposes setOk / setInfo / setIdle / setError used by the dashboard.
 */
public class MetricTile extends JPanel {

    public enum Level { OK, WARN, ERROR, IDLE }

    private final JLabel titleLbl = new JLabel("Metric");
    private final JLabel valueLbl = new JLabel("-");
    private Level level = Level.IDLE;

    public MetricTile(String title) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10,12,12,12));

        titleLbl.setText(title);
        titleLbl.setFont(titleLbl.getFont().deriveFont(Font.BOLD, 14f));
        valueLbl.setFont(valueLbl.getFont().deriveFont(Font.PLAIN, 13f));

        // react to theme changes
        AppTheme.addThemeListener(() -> {
            titleLbl.setForeground(AppTheme.TEXT_PRIMARY);
            valueLbl.setForeground(AppTheme.TEXT_SECONDARY);
            repaint();
        });
        titleLbl.setForeground(AppTheme.TEXT_PRIMARY);
        valueLbl.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel top = new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // glossy cap inside the card
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.GLASS_HIGHLIGHT);
                g2.fillRoundRect(6,4,getWidth()-12,10,10,10);
                g2.dispose();
            }
        };
        top.setOpaque(false);
        top.add(titleLbl, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8,0));
        center.setOpaque(false);
        center.add(valueLbl, BorderLayout.WEST);
        // right: status dot
        center.add(new JComponent() {
            @Override public Dimension getPreferredSize() { return new Dimension(20,20); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(switch (level) {
                    case OK    -> AppTheme.DOT_OK;
                    case WARN  -> AppTheme.DOT_WARN;
                    case ERROR -> AppTheme.DOT_ERROR;
                    default    -> AppTheme.DOT_IDLE;
                });
                int d = 8;
                g2.fillOval(getWidth()-d-2, (getHeight()-d)/2, d, d);
                g2.dispose();
            }
        }, BorderLayout.EAST);

        add(center, BorderLayout.CENTER);
    }

    // Glass background
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        AppTheme.paintCard((Graphics2D) g, getWidth(), getHeight());
    }

    // --- API used by AgentUIModernDashboard ------------------------
    public void setOk(String text)   { set(Level.OK,   text); }
    public void setInfo(String text) { set(Level.WARN, text); } // visually neutral/blue-ish; use WARN slot
    public void setIdle(String text) { set(Level.IDLE, text); }
    public void setError(String text){ set(Level.ERROR,text); }

    private void set(Level lvl, String text) {
        this.level = lvl;
        if (text != null) valueLbl.setText(text);
        repaint();
    }
}
