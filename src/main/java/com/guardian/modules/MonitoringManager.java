package com.guardian.modules;

import com.guardian.monitor.ExtendedMonitoringBootstrap;

/**
 * Упрощённый менеджер мониторинга.
 * Больше НЕ использует старые классы вроде WatchServiceFileActivityMonitor.
 * Вся работа делегируется в ExtendedMonitoringBootstrap.
 */
public class MonitoringManager {

    private static final MonitoringManager INSTANCE = new MonitoringManager();

    private volatile boolean started = false;

    private MonitoringManager() {
    }

    public static MonitoringManager getInstance() {
        return INSTANCE;
    }

    /**
     * Запуск всех фоновых сервисов мониторинга.
     * Можно вызывать несколько раз – повторный запуск не произойдёт.
     */
    public synchronized void start() {
        if (started) {
            return;
        }
        ExtendedMonitoringBootstrap.start();
        started = true;
    }

    /**
     * Остановка – сейчас ничего не делает, т.к. сервисы живут до завершения процесса.
     * Метод оставлен только для совместимости с остальным кодом.
     */
    public synchronized void stop() {
        // no-op
    }
}
