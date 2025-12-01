package com.guardian.ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends GlassPanel {

    private final JButton startButton;
    private final JButton stopButton;
    private final ThemeSwitch themeSwitch;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel themeLabel;

    public interface ControlListener {
        void onStart();
        void onStop();
        void onThemeChanged(ThemeMode mode);
    }

    public ControlPanel(ControlListener listener) {
        super(new BorderLayout());

        GlassCard card = new GlassCard(false);
        add(card, BorderLayout.CENTER);

        // LEFT SIDE (title + subtitle)
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("DefenSys – Windows Security Agent");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        titleLabel.setForeground(AppTheme.getTitleColor());

        subtitleLabel = new JLabel("Real-time monitoring: USB, Network & suspicious activity");
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 13f));
        subtitleLabel.setForeground(AppTheme.getSubtitleColor());

        left.add(titleLabel);
        left.add(Box.createVerticalStrut(6));
        left.add(subtitleLabel);

        // RIGHT SIDE (buttons + theme switch)
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
        right.add(Box.createHorizontalStrut(8));

        startButton = new JButton("Start agent");
        stopButton = new JButton("Stop agent");
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            if (listener != null) listener.onStart();
        });

        stopButton.addActionListener(e -> {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            if (listener != null) listener.onStop();
        });

        themeSwitch = new ThemeSwitch();
        themeSwitch.setMode(AppTheme.getMode());
        themeSwitch.setOnToggle(() -> {
            ThemeMode newMode = themeSwitch.getMode();
            AppTheme.setMode(newMode);
            if (listener != null) listener.onThemeChanged(newMode);
        });

        themeLabel = new JLabel("Theme:");
        themeLabel.setForeground(AppTheme.getSubtitleColor());

        right.add(startButton);
        right.add(Box.createHorizontalStrut(8));
        right.add(stopButton);
        right.add(Box.createHorizontalStrut(18));
        right.add(themeLabel);
        right.add(Box.createHorizontalStrut(6));
        right.add(themeSwitch);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);
    }

    public void refreshColors() {
        titleLabel.setForeground(AppTheme.getTitleColor());
        subtitleLabel.setForeground(AppTheme.getSubtitleColor());
        themeLabel.setForeground(AppTheme.getSubtitleColor());
    }
}
