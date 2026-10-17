package com.example.lumastudio.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StorageManager {

    private final Context context;
    private final SettingsManager settingsManager;

    public StorageManager(Context context) {
        this.context = context.getApplicationContext();
        this.settingsManager = new SettingsManager(this.context);
    }

    public Uri saveBitmap(Bitmap bitmap, String prefix) {
        String qualityPref = settingsManager.getExportQuality();
        int quality = 90;
        if ("Standard".equalsIgnoreCase(qualityPref)) quality = 80;
        else if ("Maximum".equalsIgnoreCase(qualityPref)) quality = 100;

        String formatPref = settingsManager.getImageFormat();
        return saveBitmapAdvanced(bitmap, prefix, formatPref, quality);
    }

    public Uri saveBitmapAdvanced(Bitmap bitmap, String prefix, String format, int quality) {
        boolean isPng = "PNG".equalsIgnoreCase(format);
        String ext = isPng ? ".png" : ".jpg";
        String mime = isPng ? "image/png" : "image/jpeg";
        Bitmap.CompressFormat compressFormat = isPng ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;

        String filename = prefix + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ext;

        if ("SAF".equals(settingsManager.getStorageMode()) && !settingsManager.getSafUri().isEmpty()) {
            try {
                Uri treeUri = Uri.parse(settingsManager.getSafUri());
                DocumentFile pickedDir = DocumentFile.fromTreeUri(context, treeUri);
                if (pickedDir != null && pickedDir.exists()) {
                    DocumentFile newFile = pickedDir.createFile(mime, filename);
                    if (newFile != null) {
                        try (OutputStream out = context.getContentResolver().openOutputStream(newFile.getUri())) {
                            bitmap.compress(compressFormat, isPng ? 100 : quality, out);
                            return newFile.getUri();
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // Default: MediaStore Pictures/Luma Photobooth
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, mime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Luma Photobooth");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        ContentResolver resolver = context.getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (imageUri != null) {
            try {
                try (OutputStream out = resolver.openOutputStream(imageUri)) {
                    bitmap.compress(compressFormat, isPng ? 100 : quality, out);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    resolver.update(imageUri, values, null, null);
                }
                return imageUri;
            } catch (Exception e) {
                resolver.delete(imageUri, null, null);
            }
        }
        return null;
    }

    public Uri saveVideoFile(File srcFile) {
        String filename = "LUMA_VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".mp4";

        if ("SAF".equals(settingsManager.getStorageMode()) && !settingsManager.getSafUri().isEmpty()) {
            try {
                Uri treeUri = Uri.parse(settingsManager.getSafUri());
                DocumentFile pickedDir = DocumentFile.fromTreeUri(context, treeUri);
                if (pickedDir != null && pickedDir.exists()) {
                    DocumentFile newFile = pickedDir.createFile("video/mp4", filename);
                    if (newFile != null) {
                        try (OutputStream out = context.getContentResolver().openOutputStream(newFile.getUri());
                             InputStream in = new java.io.FileInputStream(srcFile)) {
                            byte[] buf = new byte[8192];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            return newFile.getUri();
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Luma Photobooth");
            values.put(MediaStore.Video.Media.IS_PENDING, 1);
        }

        ContentResolver resolver = context.getContentResolver();
        Uri videoUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        if (videoUri != null) {
            try {
                try (OutputStream out = resolver.openOutputStream(videoUri);
                     InputStream in = new java.io.FileInputStream(srcFile)) {
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Video.Media.IS_PENDING, 0);
                    resolver.update(videoUri, values, null, null);
                }
                return videoUri;
            } catch (Exception e) {
                resolver.delete(videoUri, null, null);
            }
        }
        return null;
    }

    public List<MediaItem> loadLumaMedia() {
        List<MediaItem> items = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();

        // Query Photos
        String[] imgProjection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE
        };
        String imgSelection = MediaStore.Images.Media.DISPLAY_NAME + " LIKE ?";
        String[] imgArgs = new String[]{"LUMA_%"};

        try (Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imgProjection,
                imgSelection,
                imgArgs,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        )) {
            if (cursor != null) {
                int idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
                int sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idCol);
                    String name = cursor.getString(nameCol);
                    long dateAdded = cursor.getLong(dateCol);
                    long size = cursor.getLong(sizeCol);
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    items.add(new MediaItem(id, contentUri, name, dateAdded, size, false, 0));
                }
            }
        } catch (Exception ignored) {
        }

        // Query Videos
        String[] vidProjection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION
        };
        String vidSelection = MediaStore.Video.Media.DISPLAY_NAME + " LIKE ?";
        String[] vidArgs = new String[]{"LUMA_%"};

        try (Cursor cursor = resolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                vidProjection,
                vidSelection,
                vidArgs,
                MediaStore.Video.Media.DATE_ADDED + " DESC"
        )) {
            if (cursor != null) {
                int idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                int dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
                int sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                int durCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idCol);
                    String name = cursor.getString(nameCol);
                    long dateAdded = cursor.getLong(dateCol);
                    long size = cursor.getLong(sizeCol);
                    long duration = cursor.getLong(durCol);
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                    items.add(new MediaItem(id, contentUri, name, dateAdded, size, true, duration));
                }
            }
        } catch (Exception ignored) {
        }

        items.sort((a, b) -> Long.compare(b.getDateAdded(), a.getDateAdded()));
        return items;
    }

    public boolean deleteMedia(Uri uri) {
        // Safe media deletion
        try {
            return context.getContentResolver().delete(uri, null, null) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public long getAvailableStorageMB() {
        try {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return (availableBlocks * blockSize) / (1024 * 1024);
        } catch (Exception e) {
            return -1;
        }
    }

    public File createTempFile(String prefix, String suffix) {
        try {
            File dir = context.getCacheDir();
            return File.createTempFile(prefix, suffix, dir);
        } catch (Exception e) {
            return new File(context.getCacheDir(), prefix + "_" + System.currentTimeMillis() + suffix);
        }
    }
}