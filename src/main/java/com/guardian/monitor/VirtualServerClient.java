package com.guardian.monitor;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Properties;

/**
 * Client responsible for sending "agent is online" events
 * to a configurable virtual server.
 *
 * Server URL is stored in a small config file under user home:
 *   {user.home}/ElderGuardianLogs/config.properties
 *   key: server.url
 */
public final class VirtualServerClient {

    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final String SERVER_URL_KEY = "server.url";

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    // volatile – can be changed from UI at runtime
    private static volatile String serverUrl;

    static {
        serverUrl = loadServerUrlFromConfig();
    }

    private VirtualServerClient() {
    }

    /**
     * Returns current server URL used by the agent.
     */
    public static String getServerUrl() {
        return serverUrl;
    }

    /**
     * Updates server URL and persists it to config file.
     *
     * @param newUrl new server URL (must be non-empty http/https URL)
     */
    public static void setServerUrl(String newUrl) {
        if (newUrl == null) {
            return;
        }
        String trimmed = newUrl.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        serverUrl = trimmed;
        saveServerUrlToConfig(trimmed);
    }

    /**
     * Sends "agent is online" notification to configured server.
     * If server URL is not set or invalid, method simply returns.
     */
    public static void notifyOnline() throws IOException, InterruptedException {
        String url = serverUrl;
        if (url == null || url.isBlank()) {
            return; // nothing to do
        }

        String hostname = java.net.InetAddress.getLocalHost().getHostName();
        String body = "{"
                + "\"hostname\":\"" + escapeJson(hostname) + "\","
                + "\"timestamp\":\"" + Instant.now().toString() + "\""
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
    }

    // ***************** config helpers *****************

    private static File resolveConfigFile() {
        String userHome = System.getProperty("user.home", ".");
        File dir = new File(userHome, "ElderGuardianLogs");
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return new File(dir, CONFIG_FILE_NAME);
    }

    private static String loadServerUrlFromConfig() {
        File file = resolveConfigFile();
        if (!file.exists()) {
            // default: empty – user must set it from UI
            return "";
        }

        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
            return props.getProperty(SERVER_URL_KEY, "");
        } catch (IOException e) {
            return "";
        }
    }

    private static void saveServerUrlToConfig(String url) {
        File file = resolveConfigFile();
        Properties props = new Properties();
        props.setProperty(SERVER_URL_KEY, url);
        try (OutputStream out = new FileOutputStream(file)) {
            props.store(out, "ElderGuardian Agent configuration");
        } catch (IOException ignored) {
        }
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
