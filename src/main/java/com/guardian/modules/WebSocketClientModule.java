package com.guardian.modules;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class WebSocketClientModule {
    public interface Listener {
        default void onOpen() {}
        default void onMessage(String msg) {}
        default void onError(Throwable t) {}
        default void onClose() {}
    }

    private final URI uri;
    private final Listener listener;
    private WebSocket ws;

    public WebSocketClientModule(URI uri, Listener listener) {
        this.uri = uri;
        this.listener = listener;
    }

    public void connect() {
        HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(uri, new WebSocket.Listener() {
                    @Override public void onOpen(WebSocket webSocket) {
                        ws = webSocket;
                        listener.onOpen();
                        WebSocket.Listener.super.onOpen(webSocket);
                    }
                    @Override public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        listener.onMessage(data.toString());
                        return WebSocket.Listener.super.onText(webSocket, data, last);
                    }
                    @Override public void onError(WebSocket webSocket, Throwable error) {
                        listener.onError(error);
                    }
                }).exceptionally(ex -> {
                    listener.onError(ex);
                    return null;
                });
    }

    public boolean isOpen() {
        return ws != null;
    }

    public void close() {
        if (ws != null) {
            try { ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye"); } catch (Exception ignored) {}
            ws = null;
            listener.onClose();
        }
    }
}
