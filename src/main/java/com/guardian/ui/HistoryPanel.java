// HistoryPanel.java
package com.guardian.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class HistoryPanel extends JPanel implements PropertyChangeListener {

    private final JPanel listPanel = new JPanel();
    private final JLabel title;

    public HistoryPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(8, 8));

        title = new JLabel("History");
        title.setFont(AppTheme.titleFont(16f));
        title.setForeground(AppTheme.textPrimary());

        add(title, BorderLayout.NORTH);

        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUI(new GlassScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new GlassScrollBarUI());

        add(scroll, BorderLayout.CENTER);

        AppTheme.addThemeListener(this);
    }

    public void addEvent(String time, String source, String message, boolean alert) {
        GlassRow row = new GlassRow(time, source, message, alert);
        listPanel.add(row);
        listPanel.add(Box.createVerticalStrut(6));
        revalidate();
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mode".equals(evt.getPropertyName())) {
            title.setForeground(AppTheme.textPrimary());
            repaint();
        }
    }
}
