package com.guardian.ui;

import com.guardian.logging.SecurityEventLogger;
import com.guardian.logging.SecureLogManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * History panel with glass card look.
 */
public class HistoryPanel extends JPanel {

    private final JTextArea textArea;
    private final Timer timer;
    private final JLabel titleLabel;

    private static final int MAX_LINES = 500;
    private boolean darkTheme = true;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        GlassPanel card = new GlassPanel();
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        titleLabel = new JLabel("Event history");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        header.add(titleLabel, BorderLayout.WEST);

        card.add(header, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(false);
        textArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        textArea.setOpaque(false);
        textArea.setBorder(null);
        textArea.setMargin(new Insets(4, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new EmptyBorder(8, 0, 0, 0));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        updateColors();

        timer = new Timer(2000, e -> refreshLog());
        timer.setInitialDelay(0);
        timer.start();
    }

    // legacy API
    public void addEvent(String eventType, String source, String message, boolean important) {
        SecureLogManager.getInstance().logEvent(eventType, source, message);
    }

    public void refreshColors() {
        updateColors();
    }

    public void setDarkTheme(boolean dark) {
        this.darkTheme = dark;
        updateColors();
    }

    private void updateColors() {
        if (darkTheme) {
            titleLabel.setForeground(Color.WHITE);
            textArea.setForeground(Color.WHITE);
        } else {
            titleLabel.setForeground(new Color(30, 30, 30));
            textArea.setForeground(new Color(40, 40, 40));
        }
    }

    private void refreshLog() {
        File logFile = SecurityEventLogger.getLogFile();
        if (!logFile.exists()) {
            textArea.setText("");
            return;
        }

        Deque<String> lastLines = new ArrayDeque<>();

        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (lastLines.size() >= MAX_LINES) {
                    lastLines.removeFirst();
                }
                lastLines.addLast(line);
            }
        } catch (IOException e) {
            lastLines.clear();
            lastLines.add("Error reading log file: " + e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        for (String l : lastLines) {
            sb.append(l).append(System.lineSeparator());
        }

        String text = sb.toString();
        SwingUtilities.invokeLater(() -> {
            textArea.setText(text);
            textArea.setCaretPosition(Math.max(0, textArea.getDocument().getLength()));
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (timer.isRunning()) {
            timer.stop();
        }
    }
}
