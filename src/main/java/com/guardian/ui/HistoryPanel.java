package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

public class HistoryPanel extends GlassPanel {

    private final JTextArea textArea;
    private final JLabel titleLabel;

    public HistoryPanel() {
        super(new BorderLayout());

        GlassCard card = new GlassCard(true);
        add(card, BorderLayout.CENTER);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        titleLabel = new JLabel("Event history");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(AppTheme.getTitleColor());

        header.add(titleLabel, BorderLayout.WEST);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);                                   // нет чёрного фона
        textArea.setForeground(AppTheme.getHistoryTextColor());
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        textArea.setRows(8);                                         // фикс высоты, дальше скролл

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        card.add(card.createAccentColumn(), BorderLayout.WEST);

        JPanel inner = new JPanel(new BorderLayout(0, 10));
        inner.setOpaque(false);
        inner.add(header, BorderLayout.NORTH);
        inner.add(scroll, BorderLayout.CENTER);

        card.add(inner, BorderLayout.CENTER);
    }

    public void log(String msg) {
        if (textArea.getText().isEmpty()) {
            textArea.setText(msg);
        } else {
            textArea.append("\n" + msg);
        }
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void addEvent(String time, String source, String message, boolean alert) {
        String level = alert ? "ALERT" : "INFO";
        String line = String.format("%s [%s] (%s) %s", time, level, source, message);
        log(line);
    }

    public void refreshColors() {
        titleLabel.setForeground(AppTheme.getTitleColor());
        textArea.setForeground(AppTheme.getHistoryTextColor());
    }
}


