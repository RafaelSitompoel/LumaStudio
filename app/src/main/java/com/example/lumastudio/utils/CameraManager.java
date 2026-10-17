package com.example.lumastudio.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FileOutputOptions;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraManager {

    private static final String TAG = "LumaCameraManager";

    public interface PhotoCallback {
        void onSuccess(Bitmap bitmap);
        void onError(Exception e);
    }

    public interface VideoCallback {
        void onStart();
        void onProgress(long durationMs, long bytesWritten);
        void onSuccess(File videoFile);
        void onError(Exception e);
    }

    private final Context context;
    private final ExecutorService cameraExecutor;

    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private Preview preview;
    private ImageCapture imageCapture;
    private VideoCapture<Recorder> videoCapture;
    private Recording activeRecording;

    private int cameraLensFacing = CameraSelector.LENS_FACING_FRONT;
    private int flashMode = ImageCapture.FLASH_MODE_OFF;

    public CameraManager(Context context) {
        this.context = context.getApplicationContext();
        this.cameraExecutor = Executors.newSingleThreadExecutor();
    }

    public void startCamera(LifecycleOwner lifecycleOwner, PreviewView previewView, Runnable onReady) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(lifecycleOwner, previewView);
                if (onReady != null) onReady.run();
            } catch (Exception e) {
                Log.e(TAG, "Failed to start camera", e);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void bindCameraUseCases(LifecycleOwner lifecycleOwner, PreviewView previewView) {
        if (cameraProvider == null) return;

        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraLensFacing)
                .build();

        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(flashMode)
                .build();

        Recorder recorder = new Recorder.Builder().build();
        videoCapture = VideoCapture.withOutput(recorder);

        try {
            camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    videoCapture
            );
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    public void toggleCamera(LifecycleOwner lifecycleOwner, PreviewView previewView) {
        cameraLensFacing = (cameraLensFacing == CameraSelector.LENS_FACING_FRONT)
                ? CameraSelector.LENS_FACING_BACK
                : CameraSelector.LENS_FACING_FRONT;
        bindCameraUseCases(lifecycleOwner, previewView);
    }

    public int toggleFlash() {
        if (flashMode == ImageCapture.FLASH_MODE_OFF) {
            flashMode = ImageCapture.FLASH_MODE_ON;
        } else if (flashMode == ImageCapture.FLASH_MODE_ON) {
            flashMode = ImageCapture.FLASH_MODE_AUTO;
        } else {
            flashMode = ImageCapture.FLASH_MODE_OFF;
        }
        if (imageCapture != null) {
            imageCapture.setFlashMode(flashMode);
        }
        return flashMode;
    }

    public int getFlashMode() {
        return flashMode;
    }

    public void takePhoto(PhotoCallback callback) {
        if (imageCapture == null) {
            callback.onError(new IllegalStateException("ImageCapture not bound"));
            return;
        }

        imageCapture.takePicture(ContextCompat.getMainExecutor(context), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                cameraExecutor.execute(() -> {
                    try {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        int rotationDegrees = image.getImageInfo().getRotationDegrees();

                        // Mirror front camera photo if needed for natural preview orientation
                        if (cameraLensFacing == CameraSelector.LENS_FACING_FRONT) {
                            bitmap = flipAndRotateBitmap(bitmap, rotationDegrees, true);
                        } else if (rotationDegrees != 0) {
                            bitmap = rotateBitmap(bitmap, rotationDegrees);
                        }

                        image.close();
                        Bitmap finalBitmap = bitmap;
                        ContextCompat.getMainExecutor(context).execute(() -> callback.onSuccess(finalBitmap));
                    } catch (Exception e) {
                        image.close();
                        ContextCompat.getMainExecutor(context).execute(() -> callback.onError(e));
                    }
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                callback.onError(exception);
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void startVideoRecording(File outputFile, boolean recordAudio, VideoCallback callback) {
        if (videoCapture == null) {
            callback.onError(new IllegalStateException("VideoCapture not bound"));
            return;
        }

        FileOutputOptions outputOptions = new FileOutputOptions.Builder(outputFile).build();
        PendingRecording pendingRecording = videoCapture.getOutput().prepareRecording(context, outputOptions);

        if (recordAudio) {
            pendingRecording.withAudioEnabled();
        }

        activeRecording = pendingRecording.start(ContextCompat.getMainExecutor(context), event -> {
            if (event instanceof VideoRecordEvent.Start) {
                callback.onStart();
            } else if (event instanceof VideoRecordEvent.Status) {
                VideoRecordEvent.Status status = (VideoRecordEvent.Status) event;
                callback.onProgress(
                        status.getRecordingStats().getRecordedDurationNanos() / 1_000_000,
                        status.getRecordingStats().getNumBytesRecorded()
                );
            } else if (event instanceof VideoRecordEvent.Finalize) {
                VideoRecordEvent.Finalize finalize = (VideoRecordEvent.Finalize) event;
                if (!finalize.hasError()) {
                    callback.onSuccess(outputFile);
                } else {
                    callback.onError(new RuntimeException("Video recording failed error code: " + finalize.getError()));
                }
                activeRecording = null;
            }
        });
    }

    public void stopVideoRecording() {
        if (activeRecording != null) {
            activeRecording.stop();
            activeRecording = null;
        }
    }

    public boolean isRecording() {
        return activeRecording != null;
    }

    public void tapToFocus(PreviewView previewView, float x, float y) {
        if (camera == null) return;

        MeteringPointFactory factory = previewView.getMeteringPointFactory();
        MeteringPoint point = factory.createPoint(x, y);
        FocusMeteringAction action = new FocusMeteringAction.Builder(point).build();

        camera.getCameraControl().startFocusAndMetering(action);
    }

    public void setZoomRatio(float zoomRatio) {
        if (camera != null) {
            camera.getCameraControl().setZoomRatio(zoomRatio);
        }
    }

    public float getZoomRatio() {
        if (camera != null) {
            return camera.getCameraInfo().getZoomState().getValue() != null ? camera.getCameraInfo().getZoomState().getValue().getZoomRatio() : 1.0f;
        }
        return 1.0f;
    }

    public float getMaxZoomRatio() {
        if (camera != null) {
            return camera.getCameraInfo().getZoomState().getValue() != null ? camera.getCameraInfo().getZoomState().getValue().getMaxZoomRatio() : 1.0f;
        }
        return 1.0f;
    }

    public float getMinZoomRatio() {
        if (camera != null) {
            return camera.getCameraInfo().getZoomState().getValue() != null ? camera.getCameraInfo().getZoomState().getValue().getMinZoomRatio() : 1.0f;
        }
        return 1.0f;
    }

    public void setExposureCompensation(int value) {
        if (camera != null) {
            camera.getCameraControl().setExposureCompensationIndex(value);
        }
    }

    public int getExposureCompensationIndex() {
        if (camera != null) {
            return camera.getCameraInfo().getExposureState().getExposureCompensationIndex();
        }
        return 0;
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static Bitmap flipAndRotateBitmap(Bitmap source, float angle, boolean flipHorizontal) {
        Matrix matrix = new Matrix();
        if (flipHorizontal) {
            matrix.postScale(-1, 1, source.getWidth() / 2f, source.getHeight() / 2f);
        }
        if (angle != 0) {
            matrix.postRotate(angle);
        }
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void shutdown() {
        cameraExecutor.shutdown();
    }
}