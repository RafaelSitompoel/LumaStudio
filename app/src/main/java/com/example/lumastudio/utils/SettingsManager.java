package com.example.lumastudio.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private static final String PREF_NAME = "luma_settings";

    private static final String KEY_TIMER_SECONDS = "timer_seconds";
    private static final String KEY_GRID_ENABLED = "grid_enabled";
    private static final String KEY_SHUTTER_SOUND = "shutter_sound";
    private static final String KEY_VIBRATION = "vibration";
    private static final String KEY_DEFAULT_FILTER = "default_filter";
    private static final String KEY_FILTER_INTENSITY = "filter_intensity";
    private static final String KEY_STORAGE_MODE = "storage_mode"; // "INTERNAL" or "SAF"
    private static final String KEY_SAF_URI = "saf_uri";
    private static final String KEY_VIDEO_QUALITY = "video_quality"; // "1080P" or "720P"
    private static final String KEY_AUDIO_ENABLED = "audio_enabled";
    private static final String KEY_AUDIO_SOURCE = "audio_source"; // "INTERNAL_MIC", "EXTERNAL_MIC"
    private static final String KEY_AUTO_COMPRESS = "auto_compress";
    private static final String KEY_COMPRESSION_PROFILE = "compression_profile"; // "SPACE_SAVER", "BALANCED", "HIGH_QUALITY"
    private static final String KEY_THEME_MODE = "theme_mode"; // "SYSTEM", "LIGHT", "DARK"
    private static final String KEY_FPS_MODE = "fps_mode"; // "30FPS" or "60FPS"
    private static final String KEY_ACCENT_COLOR = "accent_color"; // "Midnight Teal", "Sage Green", etc.
    private static final String KEY_ANIMATION_ENABLED = "animation_enabled";
    private static final String KEY_EXPORT_QUALITY = "export_quality"; // "Standard", "High", "Maximum"
    private static final String KEY_IMAGE_FORMAT = "image_format"; // "JPEG", "PNG"
    private static final String KEY_DEFAULT_LENS = "default_lens"; // "Front", "Back"
    private static final String KEY_SERVER_IP = "server_ip";

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getTimerSeconds() {
        return prefs.getInt(KEY_TIMER_SECONDS, 0);
    }

    public void setTimerSeconds(int seconds) {
        prefs.edit().putInt(KEY_TIMER_SECONDS, seconds).apply();
    }

    public boolean isGridEnabled() {
        return prefs.getBoolean(KEY_GRID_ENABLED, false);
    }

    public void setGridEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_GRID_ENABLED, enabled).apply();
    }

    public boolean isShutterSoundEnabled() {
        return prefs.getBoolean(KEY_SHUTTER_SOUND, true);
    }

    public void setShutterSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SHUTTER_SOUND, enabled).apply();
    }

    public boolean isVibrationEnabled() {
        return prefs.getBoolean(KEY_VIBRATION, true);
    }

    public void setVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply();
    }

    public int getDefaultFilterIndex() {
        return prefs.getInt(KEY_DEFAULT_FILTER, 0);
    }

    public void setDefaultFilterIndex(int index) {
        prefs.edit().putInt(KEY_DEFAULT_FILTER, index).apply();
    }

    public int getFilterIntensity() {
        return prefs.getInt(KEY_FILTER_INTENSITY, 100);
    }

    public void setFilterIntensity(int intensity) {
        prefs.edit().putInt(KEY_FILTER_INTENSITY, intensity).apply();
    }

    public String getStorageMode() {
        return prefs.getString(KEY_STORAGE_MODE, "INTERNAL");
    }

    public void setStorageMode(String mode) {
        prefs.edit().putString(KEY_STORAGE_MODE, mode).apply();
    }

    public String getSafUri() {
        return prefs.getString(KEY_SAF_URI, "");
    }

    public void setSafUri(String uri) {
        prefs.edit().putString(KEY_SAF_URI, uri).apply();
    }

    public String getVideoQuality() {
        return prefs.getString(KEY_VIDEO_QUALITY, "1080P");
    }

    public void setVideoQuality(String quality) {
        prefs.edit().putString(KEY_VIDEO_QUALITY, quality).apply();
    }

    public boolean isAudioEnabled() {
        return prefs.getBoolean(KEY_AUDIO_ENABLED, true);
    }

    public void setAudioEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUDIO_ENABLED, enabled).apply();
    }

    public String getAudioSource() {
        return prefs.getString(KEY_AUDIO_SOURCE, "INTERNAL_MIC");
    }

    public void setAudioSource(String source) {
        prefs.edit().putString(KEY_AUDIO_SOURCE, source).apply();
    }

    public boolean isAutoCompressEnabled() {
        return prefs.getBoolean(KEY_AUTO_COMPRESS, false);
    }

    public void setAutoCompressEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_COMPRESS, enabled).apply();
    }

    public String getCompressionProfile() {
        return prefs.getString(KEY_COMPRESSION_PROFILE, "BALANCED");
    }

    public void setCompressionProfile(String profile) {
        prefs.edit().putString(KEY_COMPRESSION_PROFILE, profile).apply();
    }

    public String getThemeMode() {
        return prefs.getString(KEY_THEME_MODE, "SYSTEM");
    }

    public void setThemeMode(String theme) {
        prefs.edit().putString(KEY_THEME_MODE, theme).apply();
    }

    public String getFpsMode() {
        return prefs.getString(KEY_FPS_MODE, "60FPS");
    }

    public void setFpsMode(String mode) {
        prefs.edit().putString(KEY_FPS_MODE, mode).apply();
    }

    public String getAccentColor() {
        return prefs.getString(KEY_ACCENT_COLOR, "Midnight Teal");
    }

    public void setAccentColor(String color) {
        prefs.edit().putString(KEY_ACCENT_COLOR, color).apply();
    }

    public boolean isAnimationEnabled() {
        return prefs.getBoolean(KEY_ANIMATION_ENABLED, true);
    }

    public void setAnimationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ANIMATION_ENABLED, enabled).apply();
    }

    public String getExportQuality() {
        return prefs.getString(KEY_EXPORT_QUALITY, "High");
    }

    public void setExportQuality(String quality) {
        prefs.edit().putString(KEY_EXPORT_QUALITY, quality).apply();
    }

    public String getImageFormat() {
        return prefs.getString(KEY_IMAGE_FORMAT, "JPEG");
    }

    public void setImageFormat(String format) {
        prefs.edit().putString(KEY_IMAGE_FORMAT, format).apply();
    }

    public String getDefaultLens() {
        return prefs.getString(KEY_DEFAULT_LENS, "Front");
    }

    public void setDefaultLens(String lens) {
        prefs.edit().putString(KEY_DEFAULT_LENS, lens).apply();
    }

    public String getServerIp() {
        return prefs.getString(KEY_SERVER_IP, "https://luma-booth.trycloudflare.com");
    }

    public void setServerIp(String ip) {
        prefs.edit().putString(KEY_SERVER_IP, ip).apply();
    }

    public void resetAll() {
        prefs.edit().clear().apply();
    }
}