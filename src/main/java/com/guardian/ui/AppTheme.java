// AppTheme.java
package com.guardian.ui;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public final class AppTheme {

    private static ThemeMode mode = ThemeMode.DARK;
    private static final PropertyChangeSupport pcs =
            new PropertyChangeSupport(AppTheme.class);

    private AppTheme() {}

    public static ThemeMode getMode() {
        return mode;
    }

    public static void setMode(ThemeMode newMode) {
        if (newMode == null || newMode == mode) return;
        ThemeMode old = mode;
        mode = newMode;
        pcs.firePropertyChange("mode", old, newMode);
    }

    public static void addThemeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public static void removeThemeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    public static Color background() {
        return mode == ThemeMode.DARK
                ? new Color(10, 12, 22)
                : new Color(232, 236, 245);
    }

    public static Color backgroundAlt() {
        return mode == ThemeMode.DARK
                ? new Color(18, 20, 35)
                : new Color(243, 247, 255);
    }

    public static Color glassBackground() {
        return mode == ThemeMode.DARK
                ? new Color(255, 255, 255, 25)
                : new Color(255, 255, 255, 160);
    }

    public static Color glassBorder() {
        return mode == ThemeMode.DARK
                ? new Color(255, 255, 255, 40)
                : new Color(140, 160, 200, 140);
    }

    public static Color textPrimary() {
        return mode == ThemeMode.DARK
                ? new Color(235, 240, 255)
                : Color.BLACK;
    }

    public static Color textSecondary() {
        return mode == ThemeMode.DARK
                ? new Color(170, 180, 205)
                : new Color(70, 80, 100);
    }

    public static Color accent() {
        return mode == ThemeMode.DARK
                ? new Color(120, 183, 255)
                : new Color(60, 120, 255);
    }

    public static Color accentSoft() {
        return mode == ThemeMode.DARK
                ? new Color(120, 183, 255, 60)
                : new Color(60, 120, 255, 70);
    }

    public static Color OK() {
        return mode == ThemeMode.DARK
                ? new Color(95, 214, 143)
                : new Color(44, 164, 93);
    }

    public static Color WARN() {
        return mode == ThemeMode.DARK
                ? new Color(247, 203, 96)
                : new Color(212, 165, 60);
    }

    public static Color ERROR() {
        return mode == ThemeMode.DARK
                ? new Color(240, 103, 116)
                : new Color(210, 70, 85);
    }

    public static Font titleFont(float size) {
        return new Font("Segoe UI", Font.BOLD, (int) size);
    }

    public static Font bodyFont(float size) {
        return new Font("Segoe UI", Font.PLAIN, (int) size);
    }

    public static Paint backgroundGradient(int w, int h) {
        if (mode == ThemeMode.DARK) {
            return new GradientPaint(
                    0, 0, new Color(35, 43, 80),
                    w, h, new Color(6, 12, 30)
            );
        } else {
            return new GradientPaint(
                    0, 0, new Color(255, 255, 255),
                    w, h, new Color(222, 231, 248)
            );
        }
    }
}
