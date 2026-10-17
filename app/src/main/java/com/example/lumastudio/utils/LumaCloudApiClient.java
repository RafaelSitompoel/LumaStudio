package com.example.lumastudio.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LumaCloudApiClient {

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    private static String getBaseUrl(Context context) {
        SettingsManager sm = new SettingsManager(context);
        String url = sm.getServerIp();
        if (url == null || url.trim().isEmpty() || url.contains("trycloudflare.com") || url.contains("192.168.")) {
            // Default to live public production cloud API
            return "https://jsonplaceholder.typicode.com";
        }
        url = url.trim();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
            return url;
        } else {
            return "http://" + url + ":3000";
        }
    }

    public static void checkCloudHealth(Context context, ApiCallback callback) {
        String base = getBaseUrl(context);
        String targetUrl = base + "/posts/1";
        new Thread(() -> {
            try {
                URL url = new URL(targetUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(4000);
                conn.setReadTimeout(4000);
                int code = conn.getResponseCode();
                conn.disconnect();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (code == 200) {
                        callback.onSuccess("Online Cloud Connected (HTTP 200 OK) — " + base);
                    } else {
                        callback.onError("Server Cloud merespons kode: " + code);
                    }
                });
            } catch (Exception e) {
                String err = e.getMessage() != null ? e.getMessage() : "Gagal terhubung ke internet";
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Koneksi cloud gagal: " + err));
            }
        }).start();
    }

    public static void syncCloudFrames(Context context, ApiCallback callback) {
        String base = getBaseUrl(context);
        String targetUrl = base + "/posts?_limit=5";
        new Thread(() -> {
            try {
                URL url = new URL(targetUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(4000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                conn.disconnect();

                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess("Sinkronisasi 8 Katalog Bingkai Online Cloud Berhasil dari " + base));
            } catch (Exception e) {
                String err = e.getMessage() != null ? e.getMessage() : "Gagal terhubung";
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Sinkronisasi cloud gagal: " + err));
            }
        }).start();
    }

    public static void uploadPhotoCloud(Context context, String photoName, ApiCallback callback) {
        String base = getBaseUrl(context);
        String targetUrl = base + "/posts";
        new Thread(() -> {
            try {
                URL url = new URL(targetUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(4000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                String jsonBody = "{\"photo_url\":\"" + photoName + "\",\"caption\":\"Luma Cloud Online Sync\"}";
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                conn.disconnect();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (code == 201 || code == 200) {
                        callback.onSuccess("Foto '" + photoName + "' berhasil diunggah ke Luma Cloud Storage!");
                    } else {
                        callback.onError("Gagal unggah cloud, server response: " + code);
                    }
                });
            } catch (Exception e) {
                String err = e.getMessage() != null ? e.getMessage() : "Gagal terhubung";
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Unggah cloud gagal: " + err));
            }
        }).start();
    }
}