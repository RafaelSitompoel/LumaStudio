package com.example.lumastudio.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class LumaSessionManager {
    private static LumaSessionManager instance;
    private final List<Bitmap> sessionPhotos = new ArrayList<>();

    public static synchronized LumaSessionManager getInstance() {
        if (instance == null) {
            instance = new LumaSessionManager();
        }
        return instance;
    }

    public void clearPhotos() {
        sessionPhotos.clear();
    }

    public void addPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            sessionPhotos.add(bitmap);
        }
    }

    public List<Bitmap> getSessionPhotos() {
        return sessionPhotos;
    }
}