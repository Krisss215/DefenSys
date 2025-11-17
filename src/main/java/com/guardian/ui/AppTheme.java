package com.guardian.ui;

import java.awt.*;

public class AppTheme {

    private static ThemeMode current = ThemeMode.DARK;

    public static ThemeMode getMode() {
        return current;
    }

    public static void setMode(ThemeMode mode) {
        current = mode;
    }

    // main background
    public static Color getBackground() {
        return current == ThemeMode.DARK
                ? new Color(7, 10, 26)
                : new Color(232, 238, 255);
    }

    // gradient
    public static Color getBackgroundGradientStart() {
        return current == ThemeMode.DARK
                ? new Color(11, 17, 40)
                : new Color(230, 237, 255);
    }

    public static Color getBackgroundGradientEnd() {
        return current == ThemeMode.DARK
                ? new Color(5, 8, 20)
                : new Color(207, 218, 255);
    }

    // title text
    public static Color getTitleColor() {
        return current == ThemeMode.DARK
                ? Color.WHITE
                : Color.BLACK;
    }

    public static Color getSubtitleColor() {
        return current == ThemeMode.DARK
                ? new Color(195, 202, 233)
                : new Color(40, 50, 90);
    }

    // glass cards
    public static Color getGlassBackground() {
        return current == ThemeMode.DARK
                ? new Color(20, 26, 52, 200)
                : new Color(255, 255, 255, 230);
    }

    public static Color getGlassBorder() {
        return current == ThemeMode.DARK
                ? new Color(90, 100, 160, 120)
                : new Color(170, 185, 230, 150);
    }

    // accent bar
    public static Color getAccentStart() {
        return current == ThemeMode.DARK
                ? new Color(102, 126, 255)
                : new Color(179, 196, 255);
    }

    public static Color getAccentEnd() {
        return current == ThemeMode.DARK
                ? new Color(140, 90, 255)
                : new Color(142, 170, 255);
    }

    // history text
    public static Color getHistoryTextColor() {
        return current == ThemeMode.DARK
                ? Color.WHITE
                : new Color(10, 18, 40);
    }

    // theme switch
    public static Color getToggleTrack() {
        return current == ThemeMode.DARK
                ? new Color(33, 39, 70)
                : new Color(214, 223, 255);
    }

    public static Color getToggleThumb() {
        return current == ThemeMode.DARK
                ? new Color(244, 246, 255)
                : Color.WHITE;
    }
}
