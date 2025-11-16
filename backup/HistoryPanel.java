package com.guardian.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * History list with liquid-glass rows and theme-aware colors.
 * Title is inset to avoid "peeling" the section border.
 */
public class HistoryPanel extends JPanel {

    public enum Level { INFO, OK, WARN, ERROR, IDLE }

    private final JLabel title = new JLabel("History");
    private final JPanel list = new JPanel();
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HistoryPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        // Title line – nudged right & down a little
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setBorder(new EmptyBorder(6, 10, 4, 10));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane sp = makeScroll();
        add(sp, BorderLayout.CENTER);

        // Theme reactions
        AppTheme.addThemeListener(this::applyTheme);
        applyTheme();

        // example first line keeps list height visible even when empty
        addInfo("UI initialized");
    }

    /** Provide a pre-styled scroll pane for external placing if needed. */
    public JScrollPane makeScroll() {
        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(18);
        try {
            sp.getVerticalScrollBar().setUI(new GlassScrollBarUI());
        } catch (Throwable ignored) {}
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        return sp;
    }

    // Public API used by the dashboard
    public void addInfo(String text)  { add(Level.INFO,  text); }
    public void addOk(String text)    { add(Level.OK,    text); }
    public void addWarn(String text)  { add(Level.WARN,  text); }
    public void addError(String text) { add(Level.ERROR, text); }
    public void addIdle(String text)  { add(Level.IDLE,  text); }

    public void add(Level lvl, String text) {
        EntryRow row = new EntryRow(lvl, fmt.format(new Date()), text);
        list.add(row);
        // tiny gap between rows
        list.add(Box.createVerticalStrut(8));
        revalidate();
        SwingUtilities.invokeLater(() -> {
            Container p = getParent();
            if (p instanceof JViewport vp) {
                JScrollBar sb = ((JScrollPane) vp.getParent()).getVerticalScrollBar();
                sb.setValue(sb.getMaximum());
            }
        });
    }

    private void applyTheme() {
        title.setForeground(AppTheme.TEXT_PRIMARY);
        list.repaint();
        repaint();
    }

    // --- Row component --------------------------------------------------------

    private static class EntryRow extends JComponent {
        private final Level level;
        private final String time;
        private final String message;

        private final JLabel timeLbl = new JLabel();
        private final JLabel msgLbl  = new JLabel();

        EntryRow(Level level, String time, String message) {
            this.level   = level;
            this.time    = time;
            this.message = message;

            setLayout(new BorderLayout(10,0));
            setOpaque(false);
            setBorder(new EmptyBorder(6, 12, 6, 12));

            timeLbl.setText(time);
            timeLbl.setForeground(AppTheme.TEXT_SECONDARY);

            msgLbl.setText(message);
            msgLbl.setFont(msgLbl.getFont().deriveFont(Font.BOLD));
            msgLbl.setForeground(AppTheme.TEXT_PRIMARY);

            // Left side panel: dot + timestamp
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)) {
                @Override public boolean isOpaque() { return false; }
            };
            left.add(new Dot(level));
            left.add(timeLbl);

            add(left, BorderLayout.WEST);
            add(msgLbl, BorderLayout.CENTER);

            AppTheme.addThemeListener(() -> {
                timeLbl.setForeground(AppTheme.TEXT_SECONDARY);
                msgLbl.setForeground(AppTheme.TEXT_PRIMARY);
                repaint();
            });
        }

        @Override public Dimension getPreferredSize() {
            return new Dimension( Math.max(320, super.getPreferredSize().width), 34);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // shadow
            g2.setColor(AppTheme.GLASS_SHADOW);
            g2.fillRoundRect(4, 6, w - 8, h - 8, 12, 12);

            // glossy fill
            g2.setPaint(new GradientPaint(0,0, AppTheme.GLASS_TOP, 0,h, AppTheme.GLASS_BOTTOM));
            g2.fillRoundRect(0, 2, w - 8, h - 8, 12, 12);

            // highlight bar
            g2.setPaint(AppTheme.GLASS_HIGHLIGHT);
            g2.fillRoundRect(10, 6, w - 28, 8, 10, 10);

            // stroke
            g2.setColor(AppTheme.GLASS_STROKE);
            g2.drawRoundRect(0, 2, w - 8, h - 8, 12, 12);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class Dot extends JComponent {
        private final Level level;
        Dot(Level level) { this.level = level; setPreferredSize(new Dimension(12,12)); setMinimumSize(getPreferredSize()); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = switch (level) {
                case ERROR -> AppTheme.DOT_ERROR;
                case WARN  -> AppTheme.DOT_WARN;
                case IDLE  -> AppTheme.DOT_IDLE;
                default    -> AppTheme.DOT_OK; // INFO & OK
            };
            int d = 8;
            g2.setColor(c);
            g2.fillOval((getWidth()-d)/2, (getHeight()-d)/2, d, d);
            g2.dispose();
        }
    }
}
