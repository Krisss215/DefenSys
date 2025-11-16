package com.guardian.ui;

import com.guardian.monitor.MonitorService;

import javax.swing.*;
import java.awt.*;

/**
 * Панель мониторинга: плитки + история + кнопки.
 * Подписывается на MonitorService и обновляет UI.
 */
public class AgentUIModernDashboard extends JPanel {

    private final MonitorService monitor;

    // Плитки
    private final MetricTile networkTile  = new MetricTile("Network");
    private final MetricTile usbTile      = new MetricTile("USB");
    private final MetricTile phishingTile = new MetricTile("Phishing");
    private final MetricTile loggerTile   = new MetricTile("Logger");

    // История
    private final HistoryPanel history = new HistoryPanel();

    // Кнопки
    private final JButton btnStart = new JButton("Start monitoring");
    private final JButton btnStop  = new JButton("Stop");

    public AgentUIModernDashboard(MonitorService service) {
        this.monitor = service;

        setOpaque(false);
        setLayout(new BorderLayout(0, 16));

        // Заголовок блока
        JPanel live = GlassPanel.section("Live status");
        live.setLayout(new BorderLayout());
        live.add(buildTilesRow(), BorderLayout.CENTER);
        live.add(buildControls(), BorderLayout.SOUTH);

        JPanel historyBox = GlassPanel.section("History");
        historyBox.setLayout(new BorderLayout());
        historyBox.add(history.makeScroll(), BorderLayout.CENTER);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(live);
        center.add(Box.createVerticalStrut(10));
        center.add(historyBox);

        add(center, BorderLayout.CENTER);

        // Кнопки
        btnStart.addActionListener(e -> monitor.start());
        btnStop.addActionListener(e -> monitor.stop());
        applyButtonsEnable();

        // Подписка на события MonitorService
        monitor.addListener(new MonitorService.Listener() {
            @Override public void onMonitoringStarted() {
                onEDT(() -> {
                    history.addInfo("Monitoring started");
                    applyButtonsEnable();
                    loggerTile.setOk("active");
                    usbTile.setInfo("scanning");
                    phishingTile.setInfo("monitoring");
                });
            }
            @Override public void onMonitoringStopped() {
                onEDT(() -> {
                    history.addWarn("Monitoring stopped");
                    applyButtonsEnable();
                    loggerTile.setError("stopped");
                    usbTile.setIdle("idle");
                    phishingTile.setIdle("idle");
                });
            }
            @Override public void onNetworkUp() {
                onEDT(() -> networkTile.setOk("online"));
            }
            @Override public void onNetworkDown() {
                onEDT(() -> networkTile.setError("offline"));
            }
            @Override public void onUsbScan(String state) {
                onEDT(() -> {
                    if ("scanning".equalsIgnoreCase(state)) usbTile.setInfo("scanning");
                    else usbTile.setIdle("idle");
                });
            }
            @Override public void onPhishingState(String state) {
                onEDT(() -> {
                    if ("monitoring".equalsIgnoreCase(state)) phishingTile.setInfo("monitoring");
                    else phishingTile.setIdle("idle");
                });
            }
            @Override public void onLoggerActive(boolean active) {
                onEDT(() -> { if (active) loggerTile.setOk("active"); else loggerTile.setError("stopped"); });
            }
        });

        // Начальные подписи
        networkTile.setIdle("online");
        usbTile.setIdle("idle");
        phishingTile.setIdle("idle");
        loggerTile.setError("stopped");
    }

    private JPanel buildTilesRow() {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.insets = new Insets(10, 10, 10, 10);

        c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        row.add(networkTile,  c);
        c.gridx++; row.add(usbTile,      c);
        c.gridx++; row.add(phishingTile, c);
        c.gridx++; row.add(loggerTile,   c);

        return row;
    }

    private JPanel buildControls() {
        JPanel box = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        box.setOpaque(false);
        AppTheme.styleGlassButton(btnStart);
        AppTheme.styleGlassButton(btnStop);
        box.add(btnStart);
        box.add(btnStop);
        return box;
    }

    private void applyButtonsEnable() {
        boolean running = monitor.isRunning();
        btnStart.setEnabled(!running);
        btnStop.setEnabled(running);
    }

    private static void onEDT(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) r.run();
        else SwingUtilities.invokeLater(r);
    }
}
