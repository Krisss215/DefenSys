package com.guardian.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Компактные Cupertino-вкладки (узкие пилюли) с анимированным переключением.
 */
public class CupertinoTabs extends JPanel {

    // Узкий хедер без фоновой капсулы на всю ширину
    private final JPanel header = new JPanel(null) {
        @Override public Dimension getPreferredSize() {
            return new Dimension(Math.max(compactWidth, 120), 34);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppTheme.DARK ? new Color(255,255,255,20) : new Color(0,0,0,22));
            g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            g2.dispose();
        }
    };

    private final JPanel content = new JPanel(null);
    private final ButtonGroup group = new ButtonGroup();
    private final Map<String, JComponent> pages = new LinkedHashMap<>();

    // Анимация
    private BufferedImage fromImg, toImg;
    private float t = 1f;   // 1 — анимации нет
    private int dir = +1;
    private final Timer anim;

    // Кого вернуть в content после анимации
    private JComponent pendingTarget = null;

    // ширина хедера по сумме кнопок
    private int compactWidth = 140;

    public CupertinoTabs() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));

        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));

        JPanel headerWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerWrap.setOpaque(false);
        headerWrap.add(header);

        add(headerWrap, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        anim = new Timer(13, null);
        anim.addActionListener(e -> {
            t += 0.12f;
            if (t >= 1f) {
                t = 1f;
                // ВОЗВРАЩАЕМ настоящую панель в content
                if (pendingTarget != null) {
                    content.removeAll();
                    content.add(pendingTarget);
                    pendingTarget.setBounds(0, 0, content.getWidth(), content.getHeight());
                    pendingTarget.doLayout();
                    pendingTarget = null;
                    content.revalidate();
                    content.repaint();
                }
                fromImg = null;
                toImg = null;
                anim.stop();
            }
            repaint();
        });

        AppTheme.addThemeListener(() -> {
            for (Component c : header.getComponents()) c.repaint();
            repaint();
        });
    }

    public void addTab(String title, JComponent panel) {
        pages.put(title, panel);
        panel.setOpaque(false);

        if (pages.size() == 1) {
            content.add(panel);
            panel.setBounds(0,0,content.getWidth(),content.getHeight());
        }

        JToggleButton b = makeSegmentButton(title);
        group.add(b);
        header.add(b);
        layoutHeaderButtons();

        if (group.getSelection() == null) b.setSelected(true);
        b.addActionListener(e -> switchTo(title));
    }

    private void switchTo(String title) {
        JComponent target = pages.get(title);
        if (target == null) return;

        // Текущая видимая страница
        JComponent current = null;
        for (Component c : content.getComponents()) if (c.isVisible()) { current = (JComponent) c; break; }
        if (current == target) return;

        int w = Math.max(1, content.getWidth());
        int h = Math.max(1, content.getHeight());

        // Если контент ещё не промерен — без анимации
        if (w < 20 || h < 20) {
            content.removeAll();
            content.add(target);
            target.setBounds(0,0,w,h);
            content.revalidate();
            content.repaint();
            t = 1f;
            fromImg = toImg = null;
            pendingTarget = null;
            return;
        }

        // Подготовка буферов
        if (current != null) {
            fromImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = fromImg.createGraphics();
            current.paint(g); g.dispose();
            content.remove(current);
        } else {
            fromImg = null;
        }

        content.add(target);
        target.setBounds(0,0,w,h); target.doLayout();
        toImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = toImg.createGraphics();
        target.paint(g2); g2.dispose();
        content.remove(target);              // будем рисовать сами
        pendingTarget = target;              // и вернуть в конце

        dir = +1;
        if (current != null) {
            int ci = indexOf(current), ni = indexOf(target);
            dir = (ni > ci) ? +1 : -1;
        }
        t = 0f;
        anim.start();
    }

    private int indexOf(JComponent comp) {
        int i = 0;
        for (JComponent c : pages.values()) { if (c == comp) return i; i++; }
        return 0;
    }

    private JToggleButton makeSegmentButton(String text) {
        JToggleButton b = new JToggleButton(text) {
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = 28;
                d.width = Math.max(60, d.width);
                return d;
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int r = 14;
                int w = getWidth(), h = getHeight();

                Color text = AppTheme.TEXT_PRIMARY;
                Color fillSel   = AppTheme.DARK ? new Color(110,175,255,90) : new Color(80,145,250,90);
                Color strokeSel = AppTheme.DARK ? new Color(120,190,255,170) : new Color(65,120,230,170);
                Color hover     = AppTheme.DARK ? new Color(255,255,255,30)  : new Color(0,0,0,20);

                if (getModel().isRollover() && !isSelected()) {
                    g2.setColor(hover);
                    g2.fillRoundRect(0,0,w,h,r,r);
                }
                if (isSelected()) {
                    g2.setColor(fillSel);
                    g2.fillRoundRect(0,0,w,h,r,r);
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.setColor(strokeSel);
                    g2.drawRoundRect(0,0,w-1,h-1,r,r);
                }

                setForeground(text);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        b.setOpaque(false);
        b.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setFont(getFont().deriveFont(Font.PLAIN, 13f));
        b.setForeground(AppTheme.TEXT_PRIMARY);
        return b;
    }

    private void layoutHeaderButtons() {
        int x = 4;
        int h = 28;
        int total = 8;
        for (Component c : header.getComponents()) {
            Dimension d = c.getPreferredSize();
            c.setBounds(x, 3, d.width, h);
            x += d.width + 6;
            total += d.width + 6;
        }
        compactWidth = Math.max(140, total);
        header.revalidate();
        header.repaint();
    }

    @Override public void doLayout() {
        super.doLayout();
        for (Component c : content.getComponents())
            c.setBounds(0,0,content.getWidth(),content.getHeight());
    }

    @Override protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (fromImg == null && toImg == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        int w = content.getWidth(), h = content.getHeight();
        int cx = content.getX(), cy = content.getY();

        g2.setClip(cx, cy, w, h);

        float ease = (float) (1 - Math.pow(1 - t, 3)); // easeOutCubic
        int dx = (int) ((w + 24) * (1f - ease)) * dir;

        if (fromImg != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - ease));
            g2.drawImage(fromImg, cx - dx, cy, null);
        }
        if (toImg != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ease));
            g2.drawImage(toImg, cx + (w + 24 - dx) * dir, cy, null);
        }
        g2.dispose();
    }
}
