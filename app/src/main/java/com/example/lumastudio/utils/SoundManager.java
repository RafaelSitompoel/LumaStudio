package com.example.lumastudio.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class SoundManager {

    private static final String TAG = "LumaSoundManager";
    private static SoundManager instance;

    private SoundPool soundPool;
    private int soundId = 0;
    private boolean isLoaded = false;

    private SoundManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        soundPool.setOnLoadCompleteListener((pool, sampleId, status) -> {
            if (status == 0) {
                isLoaded = true;
                Log.d(TAG, "Camera shutter sound loaded successfully.");
            } else {
                Log.e(TAG, "Failed to load camera shutter sound, status: " + status);
            }
        });

        File wavFile = createShutterWavFile(context);
        if (wavFile != null && wavFile.exists()) {
            soundId = soundPool.load(wavFile.getAbsolutePath(), 1);
        }
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context.getApplicationContext());
        }
        return instance;
    }

    public void playShutterClick() {
        if (soundPool != null && isLoaded && soundId != 0) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
            Log.d(TAG, "Playing camera shutter sound effect.");
        } else {
            // Fallback to ToneGenerator if SoundPool not ready yet
            try {
                android.media.ToneGenerator tg = new android.media.ToneGenerator(android.media.AudioManager.STREAM_NOTIFICATION, 90);
                tg.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 80);
            } catch (Exception ignored) {
            }
        }
    }

    private File createShutterWavFile(Context context) {
        try {
            File file = new File(context.getCacheDir(), "camera_shutter.wav");
            if (file.exists() && file.length() > 44) {
                return file;
            }

            int sampleRate = 8000;
            int numSamples = 400; // 50ms duration
            int dataSize = numSamples * 2;
            int totalDataLen = dataSize + 36;
            long byteRate = sampleRate * 2;

            byte[] header = new byte[44];
            header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
            header[4] = (byte) (totalDataLen & 0xff);
            header[5] = (byte) ((totalDataLen >> 8) & 0xff);
            header[6] = (byte) ((totalDataLen >> 16) & 0xff);
            header[7] = (byte) ((totalDataLen >> 24) & 0xff);
            header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';
            header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
            header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0; // SubChunk1Size (16 for PCM)
            header[20] = 1; header[21] = 0; // AudioFormat (1 for PCM)
            header[22] = 1; header[23] = 0; // NumChannels (1 for Mono)
            header[24] = (byte) (sampleRate & 0xff);
            header[25] = (byte) ((sampleRate >> 8) & 0xff);
            header[26] = (byte) ((sampleRate >> 16) & 0xff);
            header[27] = (byte) ((sampleRate >> 24) & 0xff);
            header[28] = (byte) (byteRate & 0xff);
            header[29] = (byte) ((byteRate >> 8) & 0xff);
            header[30] = (byte) ((byteRate >> 16) & 0xff);
            header[31] = (byte) ((byteRate >> 24) & 0xff);
            header[32] = 2; header[33] = 0; // BlockAlign
            header[34] = 16; header[35] = 0; // BitsPerSample (16 bit)
            header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
            header[40] = (byte) (dataSize & 0xff);
            header[41] = (byte) ((dataSize >> 8) & 0xff);
            header[42] = (byte) ((dataSize >> 16) & 0xff);
            header[43] = (byte) ((dataSize >> 24) & 0xff);

            byte[] data = new byte[dataSize];
            for (int i = 0; i < numSamples; i++) {
                // Generate a sharp click decay wave
                double angle = i / (double) sampleRate * 400.0 * 2.0 * Math.PI;
                short sample = (short) (Math.sin(angle) * 32767.0 * Math.exp(-i / 80.0));
                data[i * 2] = (byte) (sample & 0xff);
                data[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(header);
            fos.write(data);
            fos.close();
            return file;
        } catch (Exception e) {
            Log.e(TAG, "Failed to create shutter wav file", e);
            return null;
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }
}