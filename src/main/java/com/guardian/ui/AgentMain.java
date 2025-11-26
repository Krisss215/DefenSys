package com.guardian.ui;

import javax.swing.*;

/**
 * Entry point for USB Ninja – Windows Security Agent.
 */
public class AgentMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Optional: use system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            AgentUIModern ui = new AgentUIModern();
            ui.setVisible(true);
        });
    }
}
