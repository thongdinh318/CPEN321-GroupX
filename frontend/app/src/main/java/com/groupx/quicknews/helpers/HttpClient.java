package com.groupx.quicknews.helpers;

import android.util.Log;

import com.groupx.quicknews.LoginActivity;

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
            void onResponse(Response response) throws IOException;
            void onFailure(Exception e);
        }
        // ChatGPT usage: No.
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
                    callback.onResponse(response);
                }
            });
        }

    // ChatGPT usage: No.
    public static void  getRequest(String url, ApiCallback callback) {
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
                callback.onResponse(response);
            }
        });

    }

    // ChatGPT usage: No.
    public static void  putRequest(String url, String json, ApiCallback callback) {
        MediaType JSON = MediaType.parse("application/json");

        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response);
            }
        });
    }

  
    public static void  getRequestWithJWT(String url, ApiCallback callback) {
        String jwt = LoginActivity.getJWT();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("jwt",jwt)
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
                callback.onResponse(response);
            }
        });

    }

    public static void putRequestWithJWT(String url, String json, ApiCallback callback) {
        MediaType JSON = MediaType.parse("application/json");
        String jwt = LoginActivity.getJWT();
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("jwt",jwt)
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response);
            }
        });
    }

    public static void  postRequestWithJWT(String url, String json, ApiCallback callback) {
        MediaType JSON = MediaType.parse("application/json");
        String jwt = LoginActivity.getJWT();
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("jwt", jwt)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response);
            }
        });
    }

      // ChatGPT usage: No.
    public static void deleteRequestWithJWT(String url, ApiCallback callback) {
        String jwt = LoginActivity.getJWT();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("jwt", jwt)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response);
            }
        });
    }
}
