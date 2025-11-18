package com.guardian.monitor;

/**
 * Монитор активности файловой системы (новые файлы).
 */
public interface FileActivityMonitor {

    void start();

    void stop();
}
