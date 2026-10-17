package com.example.lumastudio.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;

import java.util.Random;

public class FilterProcessor {

    public static class FilterParams {
        public int presetIndex = 0;
        public float intensity = 1.0f; // 0.0 - 1.0
        public float brightness = 0f; // -100 to 100
        public float contrast = 1.0f; // 0.5 to 1.5
        public float saturation = 1.0f; // 0.0 to 2.0
        public float temperature = 0f; // -50 to 50
        public float vignette = 0f; // 0.0 to 1.0
        public float grain = 0f; // 0.0 to 1.0
    }

    public static final String[] FILTER_NAMES = {
            "Original",
            "Vintage Film",
            "Kodak Warm",
            "Fuji Soft",
            "Retro 90s",
            "Y2K",
            "Soft Pink",
            "Creamy",
            "Warm Sunset",
            "Cool Blue",
            "Black & White",
            "Sepia",
            "Grainy Film",
            "Dreamy Glow",
            "Disposable Camera",
            "Polaroid Look"
    };

    public static Bitmap applyFilter(Bitmap src, FilterParams params) {
        if (src == null) return null;

        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        ColorMatrix cm = new ColorMatrix();

        // 1. Base Preset Matrix
        ColorMatrix presetCm = getPresetColorMatrix(params.presetIndex);
        cm.postConcat(presetCm);

        // 2. Adjustments
        // Saturation
        if (params.saturation != 1.0f) {
            ColorMatrix satMatrix = new ColorMatrix();
            satMatrix.setSaturation(params.saturation);
            cm.postConcat(satMatrix);
        }

        // Contrast & Brightness
        if (params.contrast != 1.0f || params.brightness != 0f) {
            float c = params.contrast;
            float b = params.brightness;
            float t = (1.0f - c) * 128f + b;
            ColorMatrix cbMatrix = new ColorMatrix(new float[]{
                    c, 0, 0, 0, t,
                    0, c, 0, 0, t,
                    0, 0, c, 0, t,
                    0, 0, 0, 1, 0
            });
            cm.postConcat(cbMatrix);
        }

        // Temperature (Red/Blue shift)
        if (params.temperature != 0f) {
            float rShift = params.temperature > 0 ? params.temperature * 1.5f : 0f;
            float bShift = params.temperature < 0 ? -params.temperature * 1.5f : 0f;
            ColorMatrix tempMatrix = new ColorMatrix(new float[]{
                    1, 0, 0, 0, rShift,
                    0, 1, 0, 0, 0,
                    0, 0, 1, 0, bShift,
                    0, 0, 0, 1, 0
            });
            cm.postConcat(tempMatrix);
        }

        // Intensity Blending with Original
        if (params.intensity < 1.0f) {
            float inv = 1.0f - params.intensity;
            float[] finalArray = cm.getArray();
            float[] identityArray = new ColorMatrix().getArray();
            float[] blended = new float[20];
            for (int i = 0; i < 20; i++) {
                blended[i] = finalArray[i] * params.intensity + identityArray[i] * inv;
            }
            cm = new ColorMatrix(blended);
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(src, 0, 0, paint);

        // 3. Vignette Effect
        if (params.vignette > 0.05f) {
            drawVignette(canvas, src.getWidth(), src.getHeight(), params.vignette);
        }

        // 4. Film Grain Effect
        if (params.grain > 0.05f) {
            drawGrain(canvas, result, params.grain);
        }

        return result;
    }

    private static ColorMatrix getPresetColorMatrix(int presetIndex) {
        ColorMatrix cm = new ColorMatrix();
        switch (presetIndex) {
            case 1: // Vintage Film
                cm.set(new float[]{
                        0.9f, 0.1f, 0.1f, 0, 20f,
                        0.05f, 0.85f, 0.1f, 0, 15f,
                        0.1f, 0.1f, 0.7f, 0, 30f,
                        0, 0, 0, 1, 0
                });
                break;

            case 2: // Kodak Warm
                cm.set(new float[]{
                        1.15f, 0.05f, 0f, 0, 15f,
                        0f, 1.05f, 0f, 0, 10f,
                        0f, 0f, 0.85f, 0, -10f,
                        0, 0, 0, 1, 0
                });
                break;

            case 3: // Fuji Soft
                cm.set(new float[]{
                        0.95f, 0.05f, 0f, 0, 10f,
                        0.05f, 1.05f, 0.05f, 0, 15f,
                        0f, 0.05f, 1.0f, 0, 20f,
                        0, 0, 0, 1, 0
                });
                break;

            case 4: // Retro 90s
                cm.set(new float[]{
                        1.2f, -0.1f, 0f, 0, 25f,
                        -0.05f, 1.1f, -0.05f, 0, 10f,
                        0f, -0.1f, 1.2f, 0, 15f,
                        0, 0, 0, 1, 0
                });
                break;

            case 5: // Y2K
                cm.set(new float[]{
                        1.1f, 0f, 0.2f, 0, 10f,
                        0f, 1.2f, 0.1f, 0, 20f,
                        0.1f, 0.2f, 1.3f, 0, 30f,
                        0, 0, 0, 1, 0
                });
                break;

            case 6: // Soft Pink
                cm.set(new float[]{
                        1.15f, 0.1f, 0.1f, 0, 30f,
                        0.05f, 0.95f, 0.1f, 0, 15f,
                        0.1f, 0.05f, 1.05f, 0, 25f,
                        0, 0, 0, 1, 0
                });
                break;

            case 7: // Creamy
                cm.set(new float[]{
                        1.05f, 0.05f, 0.05f, 0, 25f,
                        0.05f, 1.0f, 0.05f, 0, 25f,
                        0.05f, 0.05f, 0.85f, 0, 15f,
                        0, 0, 0, 1, 0
                });
                break;

            case 8: // Warm Sunset
                cm.set(new float[]{
                        1.3f, 0.1f, 0f, 0, 35f,
                        0.1f, 1.0f, 0f, 0, 15f,
                        0f, 0f, 0.7f, 0, -20f,
                        0, 0, 0, 1, 0
                });
                break;

            case 9: // Cool Blue
                cm.set(new float[]{
                        0.85f, 0f, 0.1f, 0, -10f,
                        0f, 0.95f, 0.1f, 0, 5f,
                        0.1f, 0.1f, 1.25f, 0, 30f,
                        0, 0, 0, 1, 0
                });
                break;

            case 10: // Black & White
                cm.setSaturation(0f);
                break;

            case 11: // Sepia
                cm.setScale(1f, 0.95f, 0.8f, 1f);
                ColorMatrix sepiaAdj = new ColorMatrix(new float[]{
                        0.393f, 0.769f, 0.189f, 0, 0,
                        0.349f, 0.686f, 0.168f, 0, 0,
                        0.272f, 0.534f, 0.131f, 0, 0,
                        0, 0, 0, 1, 0
                });
                cm.postConcat(sepiaAdj);
                break;

            case 12: // Grainy Film
                cm.set(new float[]{
                        0.95f, 0.05f, 0.05f, 0, 15f,
                        0.05f, 0.9f, 0.05f, 0, 10f,
                        0.05f, 0.05f, 0.85f, 0, 15f,
                        0, 0, 0, 1, 0
                });
                break;

            case 13: // Dreamy Glow
                cm.set(new float[]{
                        1.05f, 0.05f, 0.05f, 0, 30f,
                        0.05f, 1.05f, 0.05f, 0, 30f,
                        0.05f, 0.05f, 1.05f, 0, 30f,
                        0, 0, 0, 1, 0
                });
                break;

            case 14: // Disposable Camera
                cm.set(new float[]{
                        1.25f, -0.05f, 0f, 0, 20f,
                        0f, 1.15f, -0.05f, 0, 10f,
                        -0.05f, 0f, 0.9f, 0, 0,
                        0, 0, 0, 1, 0
                });
                break;

            case 15: // Polaroid Look
                cm.set(new float[]{
                        1.1f, 0.05f, 0f, 0, 20f,
                        0.05f, 1.0f, 0.05f, 0, 15f,
                        0f, 0.05f, 0.9f, 0, 30f,
                        0, 0, 0, 1, 0
                });
                break;

            default:
                break;
        }
        return cm;
    }

    private static void drawVignette(Canvas canvas, int width, int height, float vignetteAmount) {
        float radius = Math.max(width, height) * 0.75f;
        int alpha = (int) (vignetteAmount * 200); // max 200/255
        RadialGradient gradient = new RadialGradient(
                width / 2f,
                height / 2f,
                radius,
                new int[]{Color.TRANSPARENT, Color.argb(alpha, 0, 0, 0)},
                new float[]{0.5f, 1.0f},
                Shader.TileMode.CLAMP
        );
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setShader(gradient);
        canvas.drawRect(0, 0, width, height, p);
    }

    private static void drawGrain(Canvas canvas, Bitmap bitmap, float grainAmount) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int numDots = (int) (width * height * 0.03f * grainAmount);

        Random random = new Random();
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        for (int i = 0; i < numDots; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int alpha = random.nextInt((int) (80 * grainAmount)) + 10;
            p.setColor(Color.argb(alpha, 255, 255, 255));
            canvas.drawPoint(x, y, p);
        }
    }
}