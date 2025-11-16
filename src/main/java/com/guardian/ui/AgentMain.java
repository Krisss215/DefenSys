// AgentMain.java
package com.guardian.ui;

import javax.swing.*;

public class AgentMain {

    public static void main(String[] args) {

        // Необязательно, но делает вид более нативным под Windows
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            // стартуем сразу в тёмной теме (можешь поменять на LIGHT)
            AppTheme.setMode(ThemeMode.DARK);

            AgentUIModern frame = new AgentUIModern();
            frame.setVisible(true);
        });
    }
}

