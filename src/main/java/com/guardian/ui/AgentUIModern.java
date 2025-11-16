// AgentUIModern.java
package com.guardian.ui;

import com.guardian.monitor.MonitorService;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AgentUIModern extends JFrame implements PropertyChangeListener {

    private final StatusPanel statusPanel   = new StatusPanel();
    private final DashboardPanel dashboardPanel = new DashboardPanel();
    private final HistoryPanel historyPanel = new HistoryPanel();

    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel themeTextLabel;

    public AgentUIModern() {
        super("Elder Guardian Agent");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 640));
        setLocationRelativeTo(null);

        BackgroundPanel bg = new BackgroundPanel();
        bg.setLayout(new BorderLayout(16, 16));
        bg.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setContentPane(bg);

        titleLabel = new JLabel("ElderGuardian – Windows Security Agent");
        titleLabel.setFont(AppTheme.titleFont(21f));
        titleLabel.setForeground(AppTheme.textPrimary());

        subtitleLabel = new JLabel("Real-time monitoring: USB, Network");
        subtitleLabel.setFont(AppTheme.bodyFont(11.5f));
        subtitleLabel.setForeground(AppTheme.textSecondary());

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subtitleLabel);

        ThemeSwitch themeSwitch = new ThemeSwitch();

        themeTextLabel = new JLabel("Theme:");
        themeTextLabel.setFont(AppTheme.bodyFont(11.5f));
        themeTextLabel.setForeground(AppTheme.textSecondary());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleBox, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(themeTextLabel);
        rightHeader.add(themeSwitch);

        header.add(rightHeader, BorderLayout.EAST);

        bg.add(header, BorderLayout.NORTH);

        MonitorService monitorService = new MonitorService(
                statusPanel, dashboardPanel, historyPanel
        );
        ControlPanel controlPanel = new ControlPanel(monitorService);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(statusPanel);
        left.add(Box.createVerticalStrut(12));
        left.add(controlPanel);

        JPanel right = new JPanel(new BorderLayout(12, 12));
        right.setOpaque(false);
        right.add(dashboardPanel, BorderLayout.NORTH);
        right.add(historyPanel, BorderLayout.CENTER);

        JScrollPane rightScroll = new JScrollPane(right);
        rightScroll.setOpaque(false);
        rightScroll.getViewport().setOpaque(false);
        rightScroll.setBorder(BorderFactory.createEmptyBorder());
        rightScroll.getVerticalScrollBar().setUI(new GlassScrollBarUI());
        rightScroll.getHorizontalScrollBar().setUI(new GlassScrollBarUI());

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                left,
                rightScroll
        );
        split.setOpaque(false);
        split.setBorder(null);
        split.setResizeWeight(0.30);
        split.setContinuousLayout(true);
        bg.add(split, BorderLayout.CENTER);

        statusPanel.setNetworkOnline(false, 0);
        statusPanel.setUsbStats(0, 0);

        AppTheme.addThemeListener(this);

        pack();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mode".equals(evt.getPropertyName())) {
            titleLabel.setForeground(AppTheme.textPrimary());
            subtitleLabel.setForeground(AppTheme.textSecondary());
            themeTextLabel.setForeground(AppTheme.textSecondary());
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppTheme.setMode(ThemeMode.DARK);
            new AgentUIModern().setVisible(true);
        });
    }
}
