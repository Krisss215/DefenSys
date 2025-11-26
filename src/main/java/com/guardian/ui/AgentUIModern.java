package com.guardian.ui;

import com.guardian.modules.MonitoringManager;
import com.guardian.monitor.ReportGenerator;
import com.guardian.monitor.VirtualServerClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Main application window for USB Ninja – Windows Security Agent.
 * Contains:
 *  - header with title and control buttons
 *  - dashboard panel (status + chart + event history)
 *  - server URL configuration field
 *  - "Print report" (PDF) button
 *  - Dark/Light theme toggle
 */
public class AgentUIModern extends JFrame {

    private final DashboardPanel dashboardPanel;
    private final JTextField serverUrlField;
    private final JButton startButton;
    private final JButton stopButton;
    private final JButton printReportButton;
    private final JButton saveServerButton;
    private final JToggleButton themeToggle;

    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel serverLabel;

    private final JPanel headerPanel;
    private final JPanel bottomPanel;

    private volatile boolean monitoringStarted = false;
    private boolean darkTheme = true;

    public AgentUIModern() {
        super("USB Ninja – Windows Security Agent");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(1000, 600));

        // ===== Header with title and control buttons =====
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setOpaque(false);

        titleLabel = new JLabel("USB Ninja – Windows Security Agent");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));

        subtitleLabel = new JLabel("Real-time monitoring: USB, Network & suspicious activity");
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subtitleLabel);

        headerPanel.add(titleBox, BorderLayout.WEST);

        // ----- Right side controls -----
        JPanel controlsPanel = new JPanel();
        controlsPanel.setOpaque(false);
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));

        themeToggle = new JToggleButton("Dark");
        themeToggle.setSelected(true);
        themeToggle.addActionListener(this::onToggleTheme);

        startButton = new JButton("Start agent");
        startButton.addActionListener(this::onStartAgent);

        stopButton = new JButton("Stop agent");
        stopButton.addActionListener(this::onStopAgent);
        stopButton.setEnabled(false);

        printReportButton = new JButton("Print report");
        printReportButton.addActionListener(this::onPrintReport);

        controlsPanel.add(themeToggle);
        controlsPanel.add(Box.createHorizontalStrut(8));
        controlsPanel.add(startButton);
        controlsPanel.add(Box.createHorizontalStrut(8));
        controlsPanel.add(stopButton);
        controlsPanel.add(Box.createHorizontalStrut(8));
        controlsPanel.add(printReportButton);

        headerPanel.add(controlsPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ===== Center: main dashboard =====
        dashboardPanel = new DashboardPanel();
        add(dashboardPanel, BorderLayout.CENTER);

        // ===== Bottom: server URL settings =====
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        bottomPanel.setOpaque(false);

        JPanel serverPanel = new JPanel();
        serverPanel.setOpaque(false);
        serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.X_AXIS));

        serverLabel = new JLabel("Server URL: ");
        serverUrlField = new JTextField();
        serverUrlField.setColumns(40);
        serverUrlField.setText(VirtualServerClient.getServerUrl());

        saveServerButton = new JButton("Save");
        saveServerButton.addActionListener(this::onSaveServerUrl);

        serverPanel.add(serverLabel);
        serverPanel.add(Box.createHorizontalStrut(5));
        serverPanel.add(serverUrlField);
        serverPanel.add(Box.createHorizontalStrut(5));
        serverPanel.add(saveServerButton);

        bottomPanel.add(serverPanel, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Apply initial theme
        applyTheme();

        pack();
        setLocationRelativeTo(null);
    }

    // ================= THEME =================

    private void onToggleTheme(ActionEvent e) {
        darkTheme = themeToggle.isSelected();
        themeToggle.setText(darkTheme ? "Dark" : "Light");
        applyTheme();
    }

    private void applyTheme() {
        // main background
        Color bg = darkTheme ? new Color(0x141625) : new Color(0xF0F2F5);
        getContentPane().setBackground(bg);

        // text colors
        Color titleColor = darkTheme ? Color.WHITE : new Color(20, 20, 20);
        Color subtitleColor = darkTheme ? new Color(200, 200, 200) : new Color(90, 90, 90);
        Color labelColor = darkTheme ? Color.WHITE : new Color(40, 40, 40);

        titleLabel.setForeground(titleColor);
        subtitleLabel.setForeground(subtitleColor);
        serverLabel.setForeground(labelColor);

        // server URL field colors
        if (darkTheme) {
            serverUrlField.setBackground(new Color(0x21253A));
            serverUrlField.setForeground(Color.WHITE);
            serverUrlField.setCaretColor(Color.WHITE);
            serverUrlField.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 110)));
        } else {
            serverUrlField.setBackground(Color.WHITE);
            serverUrlField.setForeground(Color.BLACK);
            serverUrlField.setCaretColor(Color.BLACK);
            serverUrlField.setBorder(UIManager.getBorder("TextField.border"));
        }

        // propagate theme to dashboard (status + history)
        dashboardPanel.setDarkTheme(darkTheme);

        repaint();
    }

    // ================= ACTIONS =================

    private void onStartAgent(ActionEvent e) {
        if (monitoringStarted) {
            return;
        }
        MonitoringManager.getInstance().start();
        monitoringStarted = true;
        dashboardPanel.getHistoryPanel()
                .addEvent("INFO", "Agent", "Monitoring started", false);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void onStopAgent(ActionEvent e) {
        if (!monitoringStarted) {
            return;
        }
        MonitoringManager.getInstance().stop();
        monitoringStarted = false;
        dashboardPanel.getHistoryPanel()
                .addEvent("INFO", "Agent", "Monitoring stopped", false);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void onSaveServerUrl(ActionEvent e) {
        String url = serverUrlField.getText();
        VirtualServerClient.setServerUrl(url);
        dashboardPanel.getHistoryPanel()
                .addEvent("INFO", "UI", "Server URL updated to: " + url, false);
        JOptionPane.showMessageDialog(
                this,
                "Server URL has been saved.",
                "Server configuration",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void onPrintReport(ActionEvent e) {
        try {
            ReportGenerator generator = new ReportGenerator();
            // This now creates PDF report
            File report = generator.generateReportForLast14Days();
            if (report == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No log file found. There is nothing to include in the report.",
                        "Print report",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            dashboardPanel.getHistoryPanel()
                    .addEvent("REPORT", "UI", "Report generated: " + report.getAbsolutePath(), false);

            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(report);
                } catch (Exception openEx) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Report file created:\n" + report.getAbsolutePath(),
                            "Print report",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Report file created:\n" + report.getAbsolutePath(),
                        "Print report",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to generate report: " + ex.getMessage(),
                    "Print report",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
