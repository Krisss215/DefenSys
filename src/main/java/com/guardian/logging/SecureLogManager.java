// src/main/java/com/guardian/logging/SecureLogManager.java
package com.guardian.logging;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SecureLogManager {

    private static final int MAX_ENTRIES = 1000;

    private static final SecureLogManager INSTANCE = new SecureLogManager();

    private final LinkedList<SecureLogEntry> entries = new LinkedList<>();

    private SecureLogManager() {
    }

    public static SecureLogManager getInstance() {
        return INSTANCE;
    }

    /**
     * Лог события в UI-историю и в файл (SecurityEventLogger).
     *
     * @param eventType тип события (USB / NET / UI / и т.п.)
     * @param source    источник (например "Agent", "UI")
     * @param message   текст сообщения
     * @return созданная запись
     */
    public synchronized SecureLogEntry logEvent(String eventType,
                                                String source,
                                                String message) {
        SecureLogEntry entry = new SecureLogEntry(eventType, source, message);

        // кладём в список для отображения в UI
        entries.addFirst(entry);
        while (entries.size() > MAX_ENTRIES) {
            entries.removeLast();
        }

        // дублируем в файловый лог для отчётов / анализа
        SecurityEventLogger.log(eventType, "[" + source + "] " + message);

        return entry;
    }

    /**
     * Возвращает список записей для истории событий в UI.
     */
    public synchronized List<SecureLogEntry> getEntries() {
        return Collections.unmodifiableList(new LinkedList<>(entries));
    }
}
