package com.example.lumastudio.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FrameRenderer {

    public static class FrameParams {
        public boolean isVertical = true;
        public int frameColor = Color.parseColor("#FAF6F0");
        public int backgroundColor = Color.parseColor("#FAF6F0");
        public String patternName = "Solid";
        public int borderWidthPx = 40;
        public int spacingPx = 30;
        public String captionText = "Luma Photobooth ♡";
        public boolean showDate = true;
        public int photoCornerRadiusPx = 16;
    }

    public static Bitmap createPhotostrip(List<Bitmap> photos, FrameParams params) {
        if (photos == null || photos.isEmpty()) return null;

        int numPhotos = photos.size();
        int targetPhotoWidth = 800;
        int targetPhotoHeight = 1066; // 3:4 ratio

        int stripWidth;
        int stripHeight;
        int captionAreaHeight = (params.captionText.isEmpty() && !params.showDate) ? 80 : 220;

        if (params.isVertical) {
            stripWidth = targetPhotoWidth + (params.borderWidthPx * 2);
            stripHeight = (targetPhotoHeight * numPhotos) + (params.spacingPx * (numPhotos - 1)) + (params.borderWidthPx * 2) + captionAreaHeight;
        } else {
            stripWidth = (targetPhotoWidth * numPhotos) + (params.spacingPx * (numPhotos - 1)) + (params.borderWidthPx * 2);
            stripHeight = targetPhotoHeight + (params.borderWidthPx * 2) + captionAreaHeight;
        }

        Bitmap stripBitmap = Bitmap.createBitmap(stripWidth, stripHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(stripBitmap);

        // 1. Draw Background Frame Color
        canvas.drawColor(params.frameColor);

        // 2. Draw Photos
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        for (int i = 0; i < numPhotos; i++) {
            Bitmap srcPhoto = photos.get(i);
            int left, top, right, bottom;

            if (params.isVertical) {
                left = params.borderWidthPx;
                top = params.borderWidthPx + i * (targetPhotoHeight + params.spacingPx);
            } else {
                left = params.borderWidthPx + i * (targetPhotoWidth + params.spacingPx);
                top = params.borderWidthPx;
            }

            right = left + targetPhotoWidth;
            bottom = top + targetPhotoHeight;

            RectF destRect = new RectF(left, top, right, bottom);

            // Draw photo cropped/scaled to target aspect ratio
            Bitmap scaledPhoto = cropAndScaleBitmap(srcPhoto, targetPhotoWidth, targetPhotoHeight);
            if (params.photoCornerRadiusPx > 0) {
                drawRoundedBitmap(canvas, scaledPhoto, destRect, params.photoCornerRadiusPx);
            } else {
                canvas.drawBitmap(scaledPhoto, null, destRect, paint);
            }
        }

        // 3. Draw Caption Text & Date
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Auto contrast text color based on frame background luminance
        double luminance = (0.299 * Color.red(params.frameColor) + 0.587 * Color.green(params.frameColor) + 0.114 * Color.blue(params.frameColor)) / 255;
        int textColor = luminance > 0.5 ? Color.parseColor("#1A181B") : Color.WHITE;
        textPaint.setColor(textColor);

        int textX = stripWidth / 2;
        int textY;

        if (params.isVertical) {
            textY = stripHeight - captionAreaHeight + 100;
        } else {
            textY = stripHeight - 80;
        }

        if (!params.captionText.isEmpty()) {
            textPaint.setTextSize(52f);
            textPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
            canvas.drawText(params.captionText, textX, textY, textPaint);
        }

        if (params.showDate) {
            textPaint.setTextSize(32f);
            textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
            String dateStr = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
            int dateY = params.captionText.isEmpty() ? textY : textY + 50;
            canvas.drawText(dateStr, textX, dateY, textPaint);
        }

        return stripBitmap;
    }

    private static Bitmap cropAndScaleBitmap(Bitmap src, int reqWidth, int reqHeight) {
        if (src.getWidth() == reqWidth && src.getHeight() == reqHeight) return src;

        float srcAspect = (float) src.getWidth() / src.getHeight();
        float reqAspect = (float) reqWidth / reqHeight;

        int cropW, cropH;
        if (srcAspect > reqAspect) {
            cropH = src.getHeight();
            cropW = (int) (cropH * reqAspect);
        } else {
            cropW = src.getWidth();
            cropH = (int) (cropW / reqAspect);
        }

        int startX = (src.getWidth() - cropW) / 2;
        int startY = (src.getHeight() - cropH) / 2;

        Bitmap cropped = Bitmap.createBitmap(src, startX, startY, cropW, cropH);
        return Bitmap.createScaledBitmap(cropped, reqWidth, reqHeight, true);
    }

    private static void drawRoundedBitmap(Canvas canvas, Bitmap bitmap, RectF rect, float cornerRadius) {
        Bitmap output = Bitmap.createBitmap((int) rect.width(), (int) rect.height(), Bitmap.Config.ARGB_8888);
        Canvas roundCanvas = new Canvas(output);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        RectF roundRect = new RectF(0, 0, rect.width(), rect.height());

        roundCanvas.drawRoundRect(roundRect, cornerRadius, cornerRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        roundCanvas.drawBitmap(bitmap, null, roundRect, paint);

        canvas.drawBitmap(output, rect.left, rect.top, null);
    }
}