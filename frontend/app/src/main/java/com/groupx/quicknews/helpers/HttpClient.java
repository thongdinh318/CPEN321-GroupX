package com.groupx.quicknews.helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
        private static OkHttpClient client = new OkHttpClient();
        final static String TAG = "HTTP_Client";

        public interface ApiCallback {
            void onResponse(Response response);
            void onFailure(Exception e);
        }
        public static void  postRequest(String url, String json, ApiCallback callback) {
            MediaType JSON = MediaType.parse("application/json");

            RequestBody requestBody = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailure(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, response.body().string());
                        callback.onResponse(response);
                    }
                });
        }

    public static void  getRequest(String url, String json, ApiCallback callback) throws IOException {
        MediaType JSON = MediaType.parse("application/json");

        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "exception", e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG ,response.body().string());
                callback.onResponse(response);
            }
        });

    }
}
