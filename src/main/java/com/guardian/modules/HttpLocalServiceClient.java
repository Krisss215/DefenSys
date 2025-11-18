
package com.guardian.modules;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Отправка событий в локальный HTTP-сервис (например, на 127.0.0.1).
 * Для SA-машин можно не использовать.
 */
public class HttpLocalServiceClient implements LocalServiceClient {

    private final String endpointUrl;

    public HttpLocalServiceClient(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    @Override
    public void sendEvent(SecurityEvent event) {
        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1500);
            conn.setReadTimeout(1500);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String json = toJson(event);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(bytes);
            }

            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
            // ошибки HTTP игнорируем, чтобы не ломать агент
        }
    }

    private String toJson(SecurityEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"timestamp\":\"")
                .append(DateTimeFormatter.ISO_INSTANT.format(event.getTimestamp()))
                .append("\",");
        sb.append("\"type\":\"")
                .append(event.getType().name())
                .append("\",");
        sb.append("\"message\":")
                .append(escapeJson(event.getMessage()))
                .append(",");
        sb.append("\"details\":{");
        boolean first = true;
        for (Map.Entry<String, String> entry : event.getDetails().entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\"")
                    .append(escapeJsonString(entry.getKey()))
                    .append("\":")
                    .append(escapeJson(entry.getValue()));
            first = false;
        }
        sb.append("}");
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + escapeJsonString(value) + "\"";
    }

    private String escapeJsonString(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
