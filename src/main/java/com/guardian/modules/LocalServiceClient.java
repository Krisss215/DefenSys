package com.guardian.modules;

/**
 * Клиент для отправки событий в локальный сервис (файл, HTTP и т.д.).
 */
public interface LocalServiceClient {

    void sendEvent(SecurityEvent event);

}

