package com.funo.app.websocket;

import com.funo.app.entities.Message;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketManager {
    private static final String WEBSOCKET_URL = "ws://10.0.2.2:3000";
    private WebSocket webSocket;
    private MessageListener messageListener;
    private final OkHttpClient client;
    private final Gson gson = new Gson();

    public interface MessageListener {
        void onMessage(Message message);
    }

    public WebSocketManager(MessageListener listener) {
        this.client = new OkHttpClient();
        this.messageListener = listener;
    }

    public void connect(String roomId) {
        Request request = new Request.Builder().url(WEBSOCKET_URL + "/" + roomId).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                // Connection opened
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Message message = gson.fromJson(text, Message.class);
                messageListener.onMessage(message);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // Handle failure
            }
        });
    }

    public void sendMessage(String messageText) {
        if (webSocket != null) {
            webSocket.send(messageText);
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "User disconnected");
        }
    }
}