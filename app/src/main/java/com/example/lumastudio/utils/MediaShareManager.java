package com.example.lumastudio.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class MediaShareManager {

    public static void shareMedia(Context context, Uri mediaUri, boolean isVideo) {
        if (context == null || mediaUri == null) return;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(isVideo ? "video/*" : "image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, mediaUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(shareIntent, "Bagikan ke:"));
    }
}