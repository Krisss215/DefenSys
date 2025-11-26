package com.guardian.logging;

import java.util.List;

/**
 * Простой обёртка для получения последних лог-записей
 * для отображения в UI (история событий).
 */
public class LogCollector {

    /**
     * Возвращает список последних записей журнала.
     * Используется HistoryPanel / UI.
     */
    public List<SecureLogEntry> getRecentEntries() {
        return SecureLogManager.getInstance().getEntries();
    }
}

