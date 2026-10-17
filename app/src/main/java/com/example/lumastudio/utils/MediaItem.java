package com.example.lumastudio.utils;

import android.net.Uri;

public class MediaItem {
    private final long id;
    private final Uri uri;
    private final String name;
    private final long dateAdded;
    private final long size;
    private final boolean isVideo;
    private final long durationMs;

    public MediaItem(long id, Uri uri, String name, long dateAdded, long size, boolean isVideo, long durationMs) {
        this.id = id;
        this.uri = uri;
        this.name = name;
        this.dateAdded = dateAdded;
        this.size = size;
        this.isVideo = isVideo;
        this.durationMs = durationMs;
    }

    public long getId() {
        return id;
    }

    public Uri getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public long getSize() {
        return size;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public long getDurationMs() {
        return durationMs;
    }
}