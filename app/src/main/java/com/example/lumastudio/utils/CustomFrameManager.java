package com.example.lumastudio.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class CustomFrameManager {
    private static final String PREF_NAME = "luma_frames_pref";
    private final SharedPreferences prefs;

    public CustomFrameManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<FramePreset> loadPresets() {
        List<FramePreset> presets = new ArrayList<>();
        // Inspired by Korean-style aesthetic photobooth frames in the reference image
        presets.add(new FramePreset("Korean Pastel", Color.parseColor("#FFFDF9"), Color.parseColor("#F8E8EE"), 40, 30, true, 3, "Korean Pastel", 0));
        presets.add(new FramePreset("Coquette Heart", Color.parseColor("#E8A0BF"), Color.parseColor("#FDF2F5"), 48, 32, true, 3, "Coquette Hearts", 1));
        presets.add(new FramePreset("Retro Checkerboard", Color.parseColor("#1A181B"), Color.parseColor("#FAF6F0"), 36, 24, true, 4, "Vintage Retro", 0));
        presets.add(new FramePreset("Lavender Dream", Color.parseColor("#E8E0F0"), Color.parseColor("#F5F3F7"), 44, 28, true, 3, "Korean Pastel", 2));
        presets.add(new FramePreset("Minimalist Cream", Color.parseColor("#FAF6F0"), Color.parseColor("#FFFDF9"), 40, 30, true, 2, "Solid", 0));
        presets.add(new FramePreset("Classic Strip 6", Color.parseColor("#221F26"), Color.parseColor("#FAF6F0"), 30, 20, true, 6, "Solid", 0));
        return presets;
    }

    public void savePreset(FramePreset preset) {
        // Persist custom preset in SharedPreferences / local JSON if needed
    }
}