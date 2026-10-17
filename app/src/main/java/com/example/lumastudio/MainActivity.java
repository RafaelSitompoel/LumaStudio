package com.example.lumastudio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lumastudio.utils.CameraManager;
import com.example.lumastudio.utils.LumaSessionManager;
import com.example.lumastudio.utils.SettingsManager;
import com.example.lumastudio.utils.SoundManager;
import com.example.lumastudio.utils.StorageManager;
import com.google.android.material.slider.Slider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LumaPhotobooth";
    private static final int PERMISSION_REQ_CODE = 1001;

    private enum AppMode { PHOTO, STRIP, VIDEO }

    private AppMode currentMode = AppMode.PHOTO;

    private PreviewView previewView;
    private View gridOverlay;
    private View shutterInner;
    private RelativeLayout btnShutter;
    private ImageButton btnFlash, btnGrid, btnFlipCamera, btnSettings, btnGallery, btnFilter;
    private LinearLayout btnTimer, recordingIndicator, panelProControls;
    private TextView tvTimerVal, tvCountdown, tvRecordingTime;
    private TextView tabPhoto, tabStrip, tabVideo, btnProToggle;
    private Slider sliderZoom, sliderExposure;

    private CameraManager cameraManager;
    private SettingsManager settingsManager;
    private StorageManager storageManager;

    private int selectedTimerSeconds = 0;
    private boolean isRecordingVideo = false;
    private boolean isCapturing = false;
    private boolean stripSoundPlayed = false;
    private CountDownTimer countDownTimer;

    private int photostripTargetShots = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null && getIntent().hasExtra("slot_count")) {
            photostripTargetShots = getIntent().getIntExtra("slot_count", 2);
        }

        settingsManager = new SettingsManager(this);
        storageManager = new StorageManager(this);
        cameraManager = new CameraManager(this);

        initViews();
        setupListeners();
        checkAndRequestPermissions();
    }

    private void initViews() {
        previewView = findViewById(R.id.preview_view);
        gridOverlay = findViewById(R.id.grid_overlay);
        shutterInner = findViewById(R.id.shutter_inner);
        btnShutter = findViewById(R.id.btn_shutter);
        btnFlash = findViewById(R.id.btn_flash);
        btnTimer = findViewById(R.id.btn_timer);
        tvTimerVal = findViewById(R.id.tv_timer_val);
        btnGrid = findViewById(R.id.btn_grid);
        btnFlipCamera = findViewById(R.id.btn_flip_camera);
        btnSettings = findViewById(R.id.btn_settings);
        btnGallery = findViewById(R.id.btn_gallery);
        btnFilter = findViewById(R.id.btn_filter);
        tvCountdown = findViewById(R.id.tv_countdown);
        recordingIndicator = findViewById(R.id.recording_indicator);
        tvRecordingTime = findViewById(R.id.tv_recording_time);

        tabPhoto = findViewById(R.id.tab_photo);
        tabStrip = findViewById(R.id.tab_strip);
        tabVideo = findViewById(R.id.tab_video);
        btnProToggle = findViewById(R.id.btn_pro_toggle);
        panelProControls = findViewById(R.id.panel_pro_controls);
        sliderZoom = findViewById(R.id.slider_zoom);
        sliderExposure = findViewById(R.id.slider_exposure);

        selectedTimerSeconds = settingsManager.getTimerSeconds();
        updateTimerText();

        gridOverlay.setVisibility(settingsManager.isGridEnabled() ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        tabPhoto.setOnClickListener(v -> setMode(AppMode.PHOTO));
        tabStrip.setOnClickListener(v -> setMode(AppMode.STRIP));
        tabVideo.setOnClickListener(v -> setMode(AppMode.VIDEO));

        btnFlipCamera.setOnClickListener(v -> cameraManager.toggleCamera(this, previewView));

        btnFlash.setOnClickListener(v -> {
            int flashMode = cameraManager.toggleFlash();
            switch (flashMode) {
                case ImageCapture.FLASH_MODE_ON:
                    btnFlash.setImageResource(R.drawable.ic_flash_on);
                    break;
                case ImageCapture.FLASH_MODE_AUTO:
                    btnFlash.setImageResource(R.drawable.ic_flash_auto);
                    break;
                default:
                    btnFlash.setImageResource(R.drawable.ic_flash_off);
                    break;
            }
        });

        btnTimer.setOnClickListener(v -> {
            if (selectedTimerSeconds == 0) selectedTimerSeconds = 3;
            else if (selectedTimerSeconds == 3) selectedTimerSeconds = 5;
            else if (selectedTimerSeconds == 5) selectedTimerSeconds = 10;
            else if (selectedTimerSeconds == 10) selectedTimerSeconds = 15;
            else selectedTimerSeconds = 0;

            settingsManager.setTimerSeconds(selectedTimerSeconds);
            updateTimerText();
        });

        btnGrid.setOnClickListener(v -> {
            boolean enabled = !settingsManager.isGridEnabled();
            settingsManager.setGridEnabled(enabled);
            gridOverlay.setVisibility(enabled ? View.VISIBLE : View.GONE);
        });

        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnGallery.setOnClickListener(v -> startActivity(new Intent(this, GalleryActivity.class)));

        btnProToggle.setOnClickListener(v -> {
            boolean isVisible = panelProControls.getVisibility() == View.VISIBLE;
            panelProControls.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        });

        sliderZoom.addOnChangeListener((slider, value, fromUser) -> cameraManager.setZoomRatio(value));
        sliderExposure.addOnChangeListener((slider, value, fromUser) -> cameraManager.setExposureCompensation((int) value));

        btnFilter.setOnClickListener(v -> {
            Intent intent = new Intent(this, PhotoEditorActivity.class);
            startActivity(intent);
        });

        btnShutter.setOnClickListener(v -> onShutterClicked());

        previewView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.performClick();
                cameraManager.tapToFocus(previewView, event.getX(), event.getY());
            }
            return true;
        });
    }

    private void updateTimerText() {
        if (selectedTimerSeconds == 0) {
            tvTimerVal.setText("Off");
        } else {
            tvTimerVal.setText(String.format(Locale.getDefault(), "%ds", selectedTimerSeconds));
        }
    }

    private void setMode(AppMode mode) {
        if (isCapturing) return;
        currentMode = mode;
        tabPhoto.setTextColor(mode == AppMode.PHOTO ? 0xFFB8E0D2 : 0xB3FFFFFF);
        tabStrip.setTextColor(mode == AppMode.STRIP ? 0xFFB8E0D2 : 0xB3FFFFFF);
        tabVideo.setTextColor(mode == AppMode.VIDEO ? 0xFFB8E0D2 : 0xB3FFFFFF);

        shutterInner.setBackgroundResource(R.drawable.bg_shutter_inner);
        int tintColor = ContextCompat.getColor(this, mode == AppMode.VIDEO ? R.color.shutter_red : R.color.luma_cream);
        shutterInner.getBackground().setTint(tintColor);
    }

    private void onShutterClicked() {
        if (isCapturing) {
            Log.w(TAG, "Capture already in progress. Ignoring shutter click.");
            return;
        }

        vibrateFeedback();

        if (currentMode == AppMode.VIDEO) {
            handleVideoToggle();
            return;
        }

        if (selectedTimerSeconds > 0) {
            startTimerCountdown(selectedTimerSeconds, this::executeCaptureMode);
        } else {
            executeCaptureMode();
        }
    }

    private void executeCaptureMode() {
        if (currentMode == AppMode.PHOTO) {
            captureSinglePhoto();
        } else if (currentMode == AppMode.STRIP) {
            Log.d(TAG, "Starting Photostrip Session. Target shots: " + photostripTargetShots);
            isCapturing = true;
            btnShutter.setEnabled(false);
            stripSoundPlayed = false;
            LumaSessionManager.getInstance().clearPhotos();
            captureStripSequence(0);
        }
    }

    private void captureSinglePhoto() {
        isCapturing = true;
        btnShutter.setEnabled(false);
        Log.d(TAG, "Capturing single photo...");

        cameraManager.takePhoto(new CameraManager.PhotoCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                isCapturing = false;
                btnShutter.setEnabled(true);
                if (settingsManager.isShutterSoundEnabled()) {
                    SoundManager.getInstance(MainActivity.this).playShutterClick();
                }
                Log.d(TAG, "Single photo captured successfully. Dimensions: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                String path = saveBitmapToTempFile(bitmap);
                Intent intent = new Intent(MainActivity.this, PhotoEditorActivity.class);
                intent.putExtra("photo_path", path);
                startActivity(intent);
            }

            @Override
            public void onError(Exception e) {
                isCapturing = false;
                btnShutter.setEnabled(true);
                Log.e(TAG, "Failed to capture single photo", e);
                Toast.makeText(MainActivity.this, "Gagal mengambil foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void captureStripSequence(int currentShot) {
        if (currentShot >= photostripTargetShots) {
            isCapturing = false;
            btnShutter.setEnabled(true);
            Log.d(TAG, "Photostrip session completed successfully. Total photos: " + LumaSessionManager.getInstance().getSessionPhotos().size());
            Intent intent = new Intent(this, PhotoStripActivity.class);
            startActivity(intent);
            return;
        }

        Log.d(TAG, "Capturing strip shot " + (currentShot + 1) + " of " + photostripTargetShots);
        Toast.makeText(this, "Foto " + (currentShot + 1) + " dari " + photostripTargetShots, Toast.LENGTH_SHORT).show();

        startTimerCountdown(3, () -> cameraManager.takePhoto(new CameraManager.PhotoCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                vibrateFeedback();
                if (settingsManager.isShutterSoundEnabled() && !stripSoundPlayed) {
                    SoundManager.getInstance(MainActivity.this).playShutterClick();
                    stripSoundPlayed = true;
                }
                Log.d(TAG, "Strip shot " + (currentShot + 1) + " captured successfully.");
                LumaSessionManager.getInstance().addPhoto(bitmap);
                captureStripSequence(currentShot + 1);
            }

            @Override
            public void onError(Exception e) {
                isCapturing = false;
                btnShutter.setEnabled(true);
                Log.e(TAG, "Failed to capture strip shot " + (currentShot + 1), e);
                Toast.makeText(MainActivity.this, "Gagal pemotretan " + (currentShot + 1) + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void handleVideoToggle() {
        if (isRecordingVideo) {
            cameraManager.stopVideoRecording();
            isRecordingVideo = false;
            recordingIndicator.setVisibility(View.GONE);
        } else {
            File tempVideo = storageManager.createTempFile("LUMA_REC_", ".mp4");
            boolean recordAudio = settingsManager.isAudioEnabled();

            cameraManager.startVideoRecording(tempVideo, recordAudio, new CameraManager.VideoCallback() {
                @Override
                public void onStart() {
                    isRecordingVideo = true;
                    recordingIndicator.setVisibility(View.VISIBLE);
                }

                @Override
                public void onProgress(long durationMs, long bytesWritten) {
                    long seconds = durationMs / 1000;
                    long mins = seconds / 60;
                    long secs = seconds % 60;
                    tvRecordingTime.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, secs));
                }

                @Override
                public void onSuccess(File videoFile) {
                    recordingIndicator.setVisibility(View.GONE);
                    Uri savedUri = storageManager.saveVideoFile(videoFile);
                    if (savedUri != null) {
                        Toast.makeText(MainActivity.this, R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                        if (settingsManager.isAutoCompressEnabled()) {
                            Intent intent = new Intent(MainActivity.this, VideoCompressionActivity.class);
                            intent.putExtra("video_uri", savedUri.toString());
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    recordingIndicator.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Gagal merekam video", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startTimerCountdown(int seconds, Runnable onFinished) {
        Log.d(TAG, "Starting countdown timer for " + seconds + " seconds.");
        tvCountdown.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(seconds * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                long currentSec = (millisUntilFinished / 1000L) + 1;
                tvCountdown.setText(String.valueOf(currentSec));
            }

            @Override
            public void onFinish() {
                tvCountdown.setVisibility(View.GONE);
                Log.d(TAG, "Countdown finished. Triggering capture.");
                onFinished.run();
            }
        }.start();
    }

    private String saveBitmapToTempFile(Bitmap bitmap) {
        try {
            File tempFile = storageManager.createTempFile("LUMA_RAW_", ".jpg");
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out);
            out.flush();
            out.close();
            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Failed to save temp bitmap", e);
            return null;
        }
    }

    private void vibrateFeedback() {
        if (!settingsManager.isVibrationEnabled()) return;
        try {
            Vibrator vibrator;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                vibrator = vibratorManager.getDefaultVibrator();
            } else {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            }
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(50);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void checkAndRequestPermissions() {
        List<String> neededPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!neededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toArray(new String[0]), PERMISSION_REQ_CODE);
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        previewView.post(() -> cameraManager.startCamera(this, previewView, null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            startCamera();
        }
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) countDownTimer.cancel();
        cameraManager.shutdown();
        LumaSessionManager.getInstance().clearPhotos();
        super.onDestroy();
    }
}