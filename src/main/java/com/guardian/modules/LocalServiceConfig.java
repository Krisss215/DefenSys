package com.guardian.modules;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Конфигурация подключения к локальному сервису.
 * Загружается из файла local-service.properties (если есть),
 * иначе используются значения по умолчанию.
 */
public class LocalServiceConfig {

    private final String type;
    private final String filePath;
    private final String httpUrl;

    public LocalServiceConfig(String type, String filePath, String httpUrl) {
        this.type = type;
        this.filePath = filePath;
        this.httpUrl = httpUrl;
    }

    public static LocalServiceConfig loadDefault() {
        Path configPath = Paths.get("local-service.properties");
        if (Files.exists(configPath)) {
            try (InputStream in = Files.newInputStream(configPath)) {
                Properties props = new Properties();
                props.load(in);

                String type = props.getProperty("type", "file").trim();
                String filePath = props.getProperty("filePath", defaultLogPath());
                String httpUrl = props.getProperty("httpUrl", "http://127.0.0.1:8080/api/events");

                return new LocalServiceConfig(type, filePath, httpUrl);
            } catch (IOException e) {
                // при ошибке читаем значения по умолчанию
            }
        }
        return new LocalServiceConfig("file", defaultLogPath(), "http://127.0.0.1:8080/api/events");
    }

    private static String defaultLogPath() {
        String userHome = System.getProperty("user.home", ".");
        return Paths.get(userHome, "ElderGuardianAgent", "sa-agent.log").toString();
    }

    public String getType() {
        return type;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getHttpUrl() {
        return httpUrl;
    }
}

