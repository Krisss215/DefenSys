package com.guardian.ui;

import javax.swing.SwingUtilities;

/**
 * Application entry point for USB Ninja / ElderGuardianAgent.
 * Starts modern UI and writes a few initial log lines.
 */
public class AgentMain {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            // UI uses DefaultAgentController inside AgentUIModern
            AgentUIModern ui = new AgentUIModern();
            ui.setVisible(true);

            // Optional: startup log lines (purely visual)
            ui.appendLog(uiTimestamp(ui) + " [INFO] [Agent] UI initialized");
            ui.appendLog(uiTimestamp(ui) + " [INFO] [Agent] Waiting for Start agent command...");
        });
    }

    // small helper so we can use the same timestamp format as inside AgentUIModern
    private static String uiTimestamp(AgentUIModern ui) {

        return java.time.LocalDateTime.now()
                .toString()
                .replace('T', ' ')
                .substring(0, 19);
    }
}
