package com.example.lumastudio.utils;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoCompressionManager {

    private static final String TAG = "VideoCompressor";

    public interface CompressionCallback {
        void onStart();
        void onProgress(int percent);
        void onSuccess(File outputFile, long originalSizeBytes, long compressedSizeBytes);
        void onError(Exception e);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isCancelled = false;

    public void compressVideo(File inputFile, File outputFile, String profile, CompressionCallback callback) {
        isCancelled = false;
        mainHandler.post(callback::onStart);

        executor.execute(() -> {
            MediaExtractor extractor = null;
            MediaMuxer muxer = null;

            try {
                long originalSize = inputFile.length();

                extractor = new MediaExtractor();
                extractor.setDataSource(inputFile.getAbsolutePath());

                int videoTrackIndex = -1;
                int audioTrackIndex = -1;
                MediaFormat inputFormat = null;

                for (int i = 0; i < extractor.getTrackCount(); i++) {
                    MediaFormat format = extractor.getTrackFormat(i);
                    String mime = format.getString(MediaFormat.KEY_MIME);
                    if (mime != null && mime.startsWith("video/")) {
                        videoTrackIndex = i;
                        inputFormat = format;
                    } else if (mime != null && mime.startsWith("audio/")) {
                        audioTrackIndex = i;
                    }
                }

                if (videoTrackIndex == -1) {
                    throw new IllegalArgumentException("No video track found in file");
                }

                muxer = new MediaMuxer(outputFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

                int muxerVideoTrack = muxer.addTrack(inputFormat);
                int muxerAudioTrack = -1;

                if (audioTrackIndex != -1) {
                    MediaFormat audioFormat = extractor.getTrackFormat(audioTrackIndex);
                    muxerAudioTrack = muxer.addTrack(audioFormat);
                }

                muxer.start();

                ByteBuffer buffer = ByteBuffer.allocate(2 * 1024 * 1024);
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                long durationUs = inputFormat.containsKey(MediaFormat.KEY_DURATION) ? inputFormat.getLong(MediaFormat.KEY_DURATION) : 1;

                // Copy Video Track
                extractor.selectTrack(videoTrackIndex);
                while (!isCancelled) {
                    int sampleSize = extractor.readSampleData(buffer, 0);
                    if (sampleSize < 0) break;

                    long sampleTime = extractor.getSampleTime();
                    int extractorFlags = extractor.getSampleFlags();
                    int codecFlags = 0;
                    if ((extractorFlags & MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
                        codecFlags |= MediaCodec.BUFFER_FLAG_KEY_FRAME;
                    }

                    bufferInfo.set(0, sampleSize, sampleTime, codecFlags);
                    muxer.writeSampleData(muxerVideoTrack, buffer, bufferInfo);

                    int progress = (int) ((sampleTime * 100) / durationUs);
                    mainHandler.post(() -> callback.onProgress(Math.min(99, Math.max(0, progress))));

                    extractor.advance();
                }

                // Copy Audio Track
                if (audioTrackIndex != -1 && !isCancelled) {
                    extractor.unselectTrack(videoTrackIndex);
                    extractor.selectTrack(audioTrackIndex);
                    while (!isCancelled) {
                        int sampleSize = extractor.readSampleData(buffer, 0);
                        if (sampleSize < 0) break;

                        long sampleTime = extractor.getSampleTime();
                        int extractorFlags = extractor.getSampleFlags();
                        int codecFlags = 0;
                        if ((extractorFlags & MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
                            codecFlags |= MediaCodec.BUFFER_FLAG_KEY_FRAME;
                        }

                        bufferInfo.set(0, sampleSize, sampleTime, codecFlags);
                        muxer.writeSampleData(muxerAudioTrack, buffer, bufferInfo);
                        extractor.advance();
                    }
                }

                if (isCancelled) {
                    outputFile.delete();
                    mainHandler.post(() -> callback.onError(new Exception("Compression cancelled")));
                    return;
                }

                long compressedSize = outputFile.length();
                mainHandler.post(() -> callback.onSuccess(outputFile, originalSize, compressedSize));

            } catch (Exception e) {
                Log.e(TAG, "Video compression error", e);
                mainHandler.post(() -> callback.onError(e));
            } finally {
                if (extractor != null) extractor.release();
                if (muxer != null) {
                    try {
                        muxer.stop();
                        muxer.release();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public void cancel() {
        isCancelled = true;
    }
}