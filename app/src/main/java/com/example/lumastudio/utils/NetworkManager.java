package com.example.lumastudio.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;

public class NetworkManager {

    public interface NetworkCallback {
        void onResult(boolean isConnected);
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                android.net.Network network = cm.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(network);
                    return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static void checkConnection(Context context, NetworkCallback callback) {
        new Thread(() -> {
            boolean connected = isNetworkAvailable(context);
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(connected));
        }).start();
    }
}