// GlassRow.java
package com.guardian.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Одна строка истории событий (время + источник + сообщение).
 * В тёмной теме текст почти белый, в светлой – тёмный.
 */
public class GlassRow extends GlassPanel implements PropertyChangeListener {

    private final JLabel timeLabel   = new JLabel();
    private final JLabel sourceLabel = new JLabel();
    private final JLabel messageLabel = new JLabel();

    public GlassRow(String time, String source, String message, boolean alert) {
        setLayout(new BorderLayout(8, 4));
        setCornerRadius(18);

        timeLabel.setText(time);
        timeLabel.setFont(AppTheme.bodyFont(11f));

        sourceLabel.setText(source);
        sourceLabel.setFont(AppTheme.bodyFont(11f));

        messageLabel.setText(message);
        messageLabel.setFont(AppTheme.bodyFont(12f));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(timeLabel, BorderLayout.WEST);
        top.add(sourceLabel, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(messageLabel, BorderLayout.CENTER);

        // начальная установка цветов под текущую тему
        updateColors(alert);

        // слушаем смену темы
        AppTheme.addThemeListener(this);
    }

    private void updateColors(boolean alert) {
        boolean dark = (AppTheme.getMode() == ThemeMode.DARK);

        if (dark) {
            // тёмная тема: почти белый текст
            timeLabel.setForeground(new Color(210, 215, 235));  // светло-серый
            messageLabel.setForeground(Color.WHITE);            // белый
        } else {
            // светлая тема: тёмный текст
            timeLabel.setForeground(new Color(90, 95, 110));
            messageLabel.setForeground(Color.BLACK);
        }

        // sourceLabel: акцент или ошибка в обеих темах
        sourceLabel.setForeground(alert ? AppTheme.ERROR() : AppTheme.accent());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mode".equals(evt.getPropertyName())) {
            // пересчёт цветов при смене темы.
            // alert берём по цвету sourceLabel (простой хак)
            boolean alert = sourceLabel.getForeground().equals(AppTheme.ERROR());
            updateColors(alert);
            repaint();
        }
    }
}
