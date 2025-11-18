package com.guardian.modules;

/**
 * Фабрика для выбора реализации локального сервиса.
 */
public class LocalServiceClientFactory {

    public static LocalServiceClient create(LocalServiceConfig config) {
        String type = config.getType();
        if (type == null) {
            type = "file";
        }
        type = type.trim().toLowerCase();

        switch (type) {
            case "http":
            case "rest":
                return new HttpLocalServiceClient(config.getHttpUrl());
            case "file":
            default:
                return new FileLocalServiceClient(config.getFilePath());
        }
    }
}
