package com.guardian.modules;

import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Простой модуль проверки подозрительных URL/номеров.
 * Это минимальный пример — расширяй по потребностям (доменные черные списки, API проверки и т.д.).
 */
public class PhishingScanner {

    // Простейший набор подозрительных доменов/подстрок (пример)
    private static final Set<String> BLACKLIST = new HashSet<>();

    // Пример регулярки для "подозрительных" коротких токенов/номеров (упрощённо)
    private static final Pattern SUSPICIOUS_PATTERN = Pattern.compile(".*(free|login|verify|secure|bank|update).*", Pattern.CASE_INSENSITIVE);

    static {
        BLACKLIST.add("1-800-fake");
        BLACKLIST.add("suspicious-domain.com");
        BLACKLIST.add("malicious.example");
        // добавляй сюда свои значения
    }

    /**
     * Проверяет, подозрителен ли input (URL или номер).
     * @param input строка (URL, номер телефона или текст)
     * @return true — подозрительно
     */
    public static boolean isSuspicious(String input) {
        if (input == null || input.isBlank()) return false;

        String normalized = input.trim().toLowerCase();

        // Быстрая проверка по подстроке/черному списку
        for (String bad : BLACKLIST) {
            if (normalized.contains(bad.toLowerCase())) {
                return true;
            }
        }

        // Проверка по регулярному шаблону (ключевые слова)
        if (SUSPICIOUS_PATTERN.matcher(normalized).matches()) {
            return true;
        }

        // Можно добавить вызов внешнего API/ML модели и т.д.
        return false;
    }

    // Пример использования:
    // boolean suspect = PhishingScanner.isSuspicious("http://secure-login.example");
}
