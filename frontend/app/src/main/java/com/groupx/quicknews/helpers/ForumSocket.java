package com.groupx.quicknews.helpers;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.util.Log;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ForumSocket extends WebSocketListener {

    private static final String SOCKET_URL = "wss://quicknews.canadacentral.cloudapp.azure.com:9000/";
    private String TAG = "ForumSocket";
    public void openWebSocket() {
        Log.d(TAG, "opening socket");
        WebSocket webSocket;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectionSpecs(listOf(
                ConnectionSpec.CLEARTEXT,
                ConnectionSpec.MODERN_TLS));

        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(SOCKET_URL)
                .build();

        webSocket = client.newWebSocket(request, this);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d(TAG, "socket opened");
        // WebSocket connection opened
        // You can send messages or perform other actions here
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        // Handle text message received
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        // Handle binary message received
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        // WebSocket is about to close
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        // WebSocket connection closed
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.d(TAG, "failed");
        if (t != null) {
            Log.d(TAG, t.toString());
        }
        if (response != null) {
            Log.d(TAG, response.toString());
        }
        // Handle failure
    }
}
