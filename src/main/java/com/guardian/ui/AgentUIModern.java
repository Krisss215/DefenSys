package com.guardian.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Modern main UI for USB Ninja agent.
 * Backend is provided via AgentController interface.
 */
public class AgentUIModern extends JFrame {

    // ---------------------------------------------------------------------
    // Backend abstraction
    // ---------------------------------------------------------------------
    public interface AgentController {
        void startAgent();
        void stopAgent();
        boolean isAgentRunning();
        void generateReport(String logText);
        void updateServerUrl(String serverUrl);
    }

    /**
     * Simple default implementation, used by the no-args constructor.
     * It just keeps "running" flag and prints actions to stdout.
     */
    public static class DefaultAgentController implements AgentController {
        private boolean running = false;
        private String serverUrl = "";

        @Override
        public void startAgent() {
            running = true;
            System.out.println("[DefaultAgentController] startAgent()");
        }

        @Override
        public void stopAgent() {
            running = false;
            System.out.println("[DefaultAgentController] stopAgent()");
        }

        @Override
        public boolean isAgentRunning() {
            return running;
        }

        @Override
        public void generateReport(String logText) {
            System.out.println("[DefaultAgentController] generateReport(), length=" + logText.length());
        }

        @Override
        public void updateServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            System.out.println("[DefaultAgentController] updateServerUrl(): " + serverUrl);
        }

        public String getServerUrl() {
            return serverUrl;
        }
    }

    // ---------------------------------------------------------------------
    // Backend / agent controller
    // ---------------------------------------------------------------------
    private final AgentController agentController;

    // ---------------------------------------------------------------------
    // UI components
    // ---------------------------------------------------------------------
    private JTextArea logArea;
    private JButton startButton;
    private JButton stopButton;
    private JButton printReportButton;
    private JToggleButton lightButton;
    private JToggleButton darkButton;
    private JTextField serverUrlField;
    private JButton saveServerButton;
    private JScrollPane logScrollPane;

    private boolean darkMode = false;

    // no-args constructor, совместимый с существующим AgentMain
    public AgentUIModern() {
        this(new DefaultAgentController());
    }

    // main constructor with explicit controller
    public AgentUIModern(AgentController agentController) {
        super("USB Ninja – Windows Security Agent");
        this.agentController = agentController;

        initLookAndFeel();
        initComponents();
        applyTheme();
        updateAgentButtons();

        if (agentController instanceof DefaultAgentController dac && serverUrlField != null) {
            serverUrlField.setText(dac.getServerUrl());
        }

        setMinimumSize(new Dimension(980, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
            }
        });
    }

    // ---------------------------------------------------------------------
    // Look & Feel
    // ---------------------------------------------------------------------
    private void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equalsIgnoreCase(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception ignored) { }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
    }

    // ---------------------------------------------------------------------
    // UI init
    // ---------------------------------------------------------------------
    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(8, 8, 8, 8));
        setContentPane(content);

        // header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel title = new JLabel("USB Ninja – Windows Security Agent");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JLabel subtitle = new JLabel("Real-time monitoring: USB, Network & suspicious activity");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));

        header.add(title);
        header.add(Box.createVerticalStrut(2));
        header.add(subtitle);

        content.add(header, BorderLayout.NORTH);

        // log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        DefaultCaret caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        logScrollPane = new JScrollPane(
                logArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        logScrollPane.setBorder(BorderFactory.createEmptyBorder());
        logScrollPane.getViewport().setBackground(Color.BLACK); // will be overridden in applyTheme

        // modern scrollbars
        JScrollBar vBar = logScrollPane.getVerticalScrollBar();
        vBar.setUnitIncrement(16);
        vBar.setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
        vBar.setUI(new ModernScrollBarUI());

        JScrollBar hBar = logScrollPane.getHorizontalScrollBar();
        hBar.setUnitIncrement(16);
        hBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 12));
        hBar.setUI(new ModernScrollBarUI());

        content.add(logScrollPane, BorderLayout.CENTER);

        // bottom
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(8, 0, 0, 0));

        bottom.add(createTopToolbar(), BorderLayout.NORTH);
        bottom.add(createServerPanel(), BorderLayout.SOUTH);

        content.add(bottom, BorderLayout.SOUTH);
    }

    private JComponent createTopToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        // theme buttons
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        themePanel.setOpaque(false);

        lightButton = new JToggleButton("Light");
        darkButton = new JToggleButton("Dark");

        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightButton);
        themeGroup.add(darkButton);

        lightButton.setSelected(true);

        lightButton.addActionListener(e -> {
            darkMode = false;
            applyTheme();
        });

        darkButton.addActionListener(e -> {
            darkMode = true;
            applyTheme();
        });

        themePanel.add(lightButton);
        themePanel.add(darkButton);

        toolbar.add(themePanel, BorderLayout.WEST);

        // agent buttons
        JPanel agentPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        agentPanel.setOpaque(false);

        startButton = new JButton("Start agent");
        stopButton = new JButton("Stop agent");
        printReportButton = new JButton("Print report");

        startButton.addActionListener(e -> onStartAgent());
        stopButton.addActionListener(e -> onStopAgent());
        printReportButton.addActionListener(e -> onPrintReport());

        agentPanel.add(startButton);
        agentPanel.add(stopButton);
        agentPanel.add(printReportButton);

        toolbar.add(agentPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JComponent createServerPanel() {
        JPanel serverPanel = new JPanel(new BorderLayout(6, 0));
        serverPanel.setOpaque(false);

        JLabel label = new JLabel("Server URL:");
        serverUrlField = new JTextField();
        saveServerButton = new JButton("Save");

        saveServerButton.addActionListener(e -> onSaveServerUrl());

        serverPanel.add(label, BorderLayout.WEST);
        serverPanel.add(serverUrlField, BorderLayout.CENTER);
        serverPanel.add(saveServerButton, BorderLayout.EAST);

        return serverPanel;
    }

    // ---------------------------------------------------------------------
    // Theme
    // ---------------------------------------------------------------------
    private void applyTheme() {
        Color bg;
        Color fg;
        Color textBg;
        Color textFg;
        Color buttonBg;
        Color buttonFg;
        Color fieldBg;
        Color fieldBorder;

        if (darkMode) {
            bg = new Color(25, 27, 31);
            fg = new Color(230, 230, 230);
            textBg = new Color(18, 18, 18);
            textFg = new Color(220, 220, 220);
            buttonBg = new Color(45, 48, 55);
            buttonFg = fg;
            fieldBg = new Color(35, 37, 42);
            fieldBorder = new Color(70, 72, 80);
            darkButton.setSelected(true);
        } else {
            bg = new Color(244, 246, 250);
            fg = new Color(25, 25, 25);
            textBg = Color.WHITE;
            textFg = Color.BLACK;
            buttonBg = new Color(230, 233, 240);
            buttonFg = fg;
            fieldBg = Color.WHITE;
            fieldBorder = new Color(190, 190, 190);
            lightButton.setSelected(true);
        }

        getContentPane().setBackground(bg);
        updateTreeColors(getContentPane(), bg, fg);

        // text area + viewport
        logArea.setBackground(textBg);
        logArea.setForeground(textFg);
        if (logScrollPane != null) {
            logScrollPane.getViewport().setBackground(textBg);
        }

        // buttons
        startButton.setBackground(buttonBg);
        stopButton.setBackground(buttonBg);
        printReportButton.setBackground(buttonBg);
        saveServerButton.setBackground(buttonBg);

        startButton.setForeground(buttonFg);
        stopButton.setForeground(buttonFg);
        printReportButton.setForeground(buttonFg);
        saveServerButton.setForeground(buttonFg);

        // server URL field
        if (serverUrlField != null) {
            serverUrlField.setBackground(fieldBg);
            serverUrlField.setForeground(textFg);
            serverUrlField.setCaretColor(textFg);
            serverUrlField.setBorder(new LineBorder(fieldBorder, 1, true));
        }

        repaint();
    }

    private void updateTreeColors(Component comp, Color bg, Color fg) {
        if (comp instanceof JPanel
                || comp instanceof JScrollPane
                || comp instanceof JViewport) {
            comp.setBackground(bg);
        }

        if (comp instanceof JLabel
                || comp instanceof JButton
                || comp instanceof JToggleButton
                || comp instanceof JTextField) {
            comp.setForeground(fg);
        }

        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                updateTreeColors(child, bg, fg);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Agent actions
    // ---------------------------------------------------------------------
    private void onStartAgent() {
        if (agentController == null) {
            appendLog("[ERROR] AgentController is not attached.");
            return;
        }
        if (!agentController.isAgentRunning()) {
            agentController.startAgent();
            appendLog(timestamp() + " [INFO] [Agent] Monitoring started");
            updateAgentButtons();
        }
    }

    private void onStopAgent() {
        if (agentController == null) {
            appendLog("[ERROR] AgentController is not attached.");
            return;
        }
        if (agentController.isAgentRunning()) {
            agentController.stopAgent();
            appendLog(timestamp() + " [INFO] [Agent] Monitoring stopped");
            updateAgentButtons();
        }
    }

    private void onPrintReport() {
        if (agentController == null) {
            appendLog("[ERROR] AgentController is not attached.");
            return;
        }

        String text = logArea.getText();
        if (text == null || text.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "There is no log data to print.",
                    "Print report",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        agentController.generateReport(text);

        try {
            printTextAsPages(text);
        } catch (PrinterException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to print report: " + ex.getMessage(),
                    "Print error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onSaveServerUrl() {
        if (agentController == null) {
            appendLog("[ERROR] AgentController is not attached.");
            return;
        }

        String url = serverUrlField.getText().trim();
        agentController.updateServerUrl(url);
        appendLog(timestamp() + " [INFO] [Agent] Server URL updated to: " + url);
    }

    private void onWindowClosing() {
        if (agentController != null && agentController.isAgentRunning()) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Agent is still running. Do you want to stop and exit?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            agentController.stopAgent();
        }
        dispose();
        System.exit(0);
    }

    private void updateAgentButtons() {
        boolean running = agentController != null && agentController.isAgentRunning();
        startButton.setEnabled(!running);
        stopButton.setEnabled(running);
    }

    // ---------------------------------------------------------------------
    // Printing helper (multi-page text)
    // ---------------------------------------------------------------------
    private void printTextAsPages(String text) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("USB Ninja Report");

        final String[] lines = text.split("\n");

        Printable printable = new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));

                int lineHeight = g2d.getFontMetrics().getHeight();
                int linesPerPage = (int) (pageFormat.getImageableHeight() / lineHeight);
                int numPages = (int) Math.ceil((double) lines.length / linesPerPage);

                if (pageIndex >= numPages) {
                    return Printable.NO_SUCH_PAGE;
                }

                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                int start = pageIndex * linesPerPage;
                int end = Math.min(start + linesPerPage, lines.length);

                int y = 0;
                for (int i = start; i < end; i++) {
                    y += lineHeight;
                    g2d.drawString(lines[i], 0, y);
                }

                return Printable.PAGE_EXISTS;
            }
        };

        job.setPrintable(printable);

        if (job.printDialog()) {
            job.print();
        }
    }

    // ---------------------------------------------------------------------
    // Logging API
    // ---------------------------------------------------------------------
    public void appendLog(String line) {
        final String text = (line != null && line.endsWith("\n")) ? line : line + "\n";
        SwingUtilities.invokeLater(() -> logArea.append(text));
    }

    private String timestamp() {
        return java.time.LocalDateTime.now()
                .toString()
                .replace('T', ' ')
                .substring(0, 19);
    }

    // ---------------------------------------------------------------------
    // Modern scrollbar UI (theme-aware)
    // ---------------------------------------------------------------------
    private class ModernScrollBarUI extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            trackColor = new Color(0, 0, 0, 0);
            thumbColor = getThumbColor();
        }

        private Color getThumbColor() {
            if (darkMode) {
                return new Color(120, 170, 210);
            } else {
                return new Color(140, 180, 220);
            }
        }

        private Color getTrackFillColor() {
            if (darkMode) {
                return new Color(40, 40, 45);
            } else {
                return new Color(220, 220, 220, 150);
            }
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(getTrackFillColor());
            g2.fillRoundRect(trackBounds.x, trackBounds.y,
                    trackBounds.width, trackBounds.height, 8, 8);
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (!c.isEnabled() || thumbBounds.width <= 0 || thumbBounds.height <= 0) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setPaint(getThumbColor());
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y,
                    thumbBounds.width, thumbBounds.height, 10, 10);

            g2.dispose();
        }
    }

    // ---------------------------------------------------------------------
    // Demo main – можно удалить, если не нужен
    // ---------------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AgentUIModern ui = new AgentUIModern();
            ui.setVisible(true);
            ui.appendLog(ui.timestamp() + " [INFO] [Agent] UI initialized");
        });
    }
}
