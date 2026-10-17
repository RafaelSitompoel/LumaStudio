package com.example.lumastudio.utils;

public class PoseAssistantManager {

    private static final String[] POSE_HINTS = {
            "Siap berpose...",
            "Senyum yang manis!",
            "Ganti pose keren!",
            "Tunjukkan gaya terbaikmu!",
            "Pose bebas dan ceria!",
            "Sempurna, tahan sebentar!"
    };

    public static String getHintForShot(int shotIndex, int totalShots) {
        if (shotIndex >= 0 && shotIndex < POSE_HINTS.length) {
            return POSE_HINTS[shotIndex];
        }
        return "Senyum!";
    }
}