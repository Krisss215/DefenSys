// AgentMain.java
package com.guardian.ui;

import javax.swing.*;

public class AgentMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Красивый ЛАФ
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            AgentUIModern ui = new AgentUIModern();
            ui.setVisible(true);
        });
    }
}
