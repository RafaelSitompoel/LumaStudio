package com.example.lumastudio.utils;

import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.Inet4Address;

public class LocalSessionManager {

    private static final String TAG = "LumaLocalSession";
    private static final int PORT = 8888;

    private static LocalSessionManager instance;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private boolean isHosting = false;
    private boolean isConnected = false;

    private final List<String> connectedMembers = new ArrayList<>();

    public interface SessionCallback {
        void onStatusChanged(String status, String info);
        void onMemberJoined(List<String> members);
    }

    private SessionCallback callback;

    public static synchronized LocalSessionManager getInstance() {
        if (instance == null) {
            instance = new LocalSessionManager();
        }
        return instance;
    }

    public void setCallback(SessionCallback callback) {
        this.callback = callback;
    }

    public static String getLocalIpAddress() {
        try {
            // First priority: Wi-Fi / Local subnet (192.168., 10., 172.)
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String ip = inetAddress.getHostAddress();
                        if (ip != null && (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172."))) {
                            return ip;
                        }
                    }
                }
            }
            // Second priority: any non-loopback IPv4
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "192.168.x.x";
    }

    public void startHosting() {
        isHosting = true;
        connectedMembers.clear();
        String myIp = getLocalIpAddress();
        connectedMembers.add("Host (You - IP: " + myIp + ")");

        if (callback != null) {
            callback.onStatusChanged("Online (Hosting)", "IP Host Anda: " + myIp + " (Port: " + PORT + ")");
            callback.onMemberJoined(connectedMembers);
        }

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                Log.d(TAG, "Server started on port " + PORT);
                while (isHosting) {
                    Socket socket = serverSocket.accept();
                    String remoteIp = socket.getInetAddress().getHostAddress();
                    connectedMembers.add("Peer (" + remoteIp + ")");
                    Log.d(TAG, "Client connected: " + remoteIp);
                    if (callback != null) {
                        callback.onMemberJoined(connectedMembers);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Hosting error or stopped", e);
            }
        }).start();
    }

    public void joinSession(String hostIp) {
        new Thread(() -> {
            if (callback != null) {
                callback.onStatusChanged("Connecting", "Menghubungkan ke Host " + hostIp + "...");
            }

            try {
                clientSocket = new Socket(hostIp, PORT);
                isConnected = true;
                connectedMembers.clear();
                connectedMembers.add("Host (" + hostIp + ")");
                connectedMembers.add("You (Client)");

                Log.d(TAG, "Connected to host: " + hostIp);
                if (callback != null) {
                    callback.onStatusChanged("Online (Connected)", "Berhasil terhubung ke Host " + hostIp);
                    callback.onMemberJoined(connectedMembers);
                }
            } catch (Exception e) {
                isConnected = false;
                Log.e(TAG, "Connection failed", e);
                String reason = e.getMessage() != null ? e.getMessage() : "Connection timeout / refused";
                if (callback != null) {
                    callback.onStatusChanged("Connection Failed", "Gagal (" + reason + "). Matikan Data Seluler/VPN, pastikan 1 Wi-Fi.");
                }
            }
        }).start();
    }

    public void disconnect() {
        isHosting = false;
        isConnected = false;
        connectedMembers.clear();

        try {
            if (serverSocket != null) serverSocket.close();
            if (clientSocket != null) clientSocket.close();
        } catch (Exception ignored) {
        }

        serverSocket = null;
        clientSocket = null;

        if (callback != null) {
            callback.onStatusChanged("Disconnected", "Sesi telah diputuskan.");
            callback.onMemberJoined(connectedMembers);
        }
    }
}