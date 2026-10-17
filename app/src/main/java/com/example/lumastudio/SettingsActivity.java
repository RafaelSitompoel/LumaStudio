package com.example.lumastudio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.lumastudio.utils.SettingsManager;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    private MaterialSwitch switchShutterSound, switchVibration, switchGrid, switchAnimation;
    private RadioGroup rgLens, rgTheme, rgAccent, rgFps, rgExportQuality, rgImageFormat;
    private TextView tvStoragePath;
    private SettingsManager settingsManager;

    private final ActivityResultLauncher<Intent> safLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri treeUri = result.getData().getData();
                    if (treeUri != null) {
                        getContentResolver().takePersistableUriPermission(
                                treeUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );
                        settingsManager.setStorageMode("SAF");
                        settingsManager.setSafUri(treeUri.toString());
                        updateStorageText();
                        Toast.makeText(this, "Folder penyimpanan kustom dipilih!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsManager = new SettingsManager(this);

        initViews();
        loadSettings();
        setupListeners();
    }

    private void initViews() {
        switchShutterSound = findViewById(R.id.switch_shutter_sound);
        switchVibration = findViewById(R.id.switch_vibration);
        switchGrid = findViewById(R.id.switch_grid);
        switchAnimation = findViewById(R.id.switch_animation);
        rgLens = findViewById(R.id.rg_lens);
        rgTheme = findViewById(R.id.rg_theme);
        rgAccent = findViewById(R.id.rg_accent);
        rgFps = findViewById(R.id.rg_fps);
        rgExportQuality = findViewById(R.id.rg_export_quality);
        rgImageFormat = findViewById(R.id.rg_image_format);
        tvStoragePath = findViewById(R.id.tv_storage_path);
    }

    private void loadSettings() {
        switchShutterSound.setChecked(settingsManager.isShutterSoundEnabled());
        switchVibration.setChecked(settingsManager.isVibrationEnabled());
        switchGrid.setChecked(settingsManager.isGridEnabled());
        switchAnimation.setChecked(settingsManager.isAnimationEnabled());

        if ("Back".equals(settingsManager.getDefaultLens())) {
            rgLens.check(R.id.rb_lens_back);
        } else {
            rgLens.check(R.id.rb_lens_front);
        }

        if ("30FPS".equals(settingsManager.getFpsMode())) {
            rgFps.check(R.id.rb_30fps);
        } else {
            rgFps.check(R.id.rb_60fps);
        }

        String theme = settingsManager.getThemeMode();
        if ("LIGHT".equals(theme)) {
            rgTheme.check(R.id.rb_theme_light);
        } else if ("DARK".equals(theme)) {
            rgTheme.check(R.id.rb_theme_dark);
        } else {
            rgTheme.check(R.id.rb_theme_system);
        }

        String accent = settingsManager.getAccentColor();
        if ("Sage Green".equals(accent)) {
            rgAccent.check(R.id.rb_accent_sage);
        } else if ("Vintage Mocha".equals(accent)) {
            rgAccent.check(R.id.rb_accent_mocha);
        } else {
            rgAccent.check(R.id.rb_accent_teal);
        }

        String exportQual = settingsManager.getExportQuality();
        if ("Standard".equals(exportQual)) {
            rgExportQuality.check(R.id.rb_export_standard);
        } else if ("Maximum".equals(exportQual)) {
            rgExportQuality.check(R.id.rb_export_max);
        } else {
            rgExportQuality.check(R.id.rb_export_high);
        }

        if ("PNG".equals(settingsManager.getImageFormat())) {
            rgImageFormat.check(R.id.rb_format_png);
        } else {
            rgImageFormat.check(R.id.rb_format_jpeg);
        }

        updateStorageText();
    }

    private void setupListeners() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        Button btnSelectSaf = findViewById(R.id.btn_select_saf);
        Button btnReset = findViewById(R.id.btn_reset);

        btnBack.setOnClickListener(v -> finish());

        switchShutterSound.setOnCheckedChangeListener((v, checked) -> {
            if (v.isPressed()) {
                settingsManager.setShutterSoundEnabled(checked);
                Toast.makeText(this, "Suara Shutter: " + (checked ? "Aktif" : "Mati"), Toast.LENGTH_SHORT).show();
            }
        });

        switchVibration.setOnCheckedChangeListener((v, checked) -> {
            if (v.isPressed()) {
                settingsManager.setVibrationEnabled(checked);
                Toast.makeText(this, "Getaran Shutter: " + (checked ? "Aktif" : "Mati"), Toast.LENGTH_SHORT).show();
            }
        });

        switchGrid.setOnCheckedChangeListener((v, checked) -> {
            if (v.isPressed()) {
                settingsManager.setGridEnabled(checked);
                Toast.makeText(this, "Grid Kamera: " + (checked ? "Aktif" : "Mati"), Toast.LENGTH_SHORT).show();
            }
        });

        switchAnimation.setOnCheckedChangeListener((v, checked) -> {
            if (v.isPressed()) {
                settingsManager.setAnimationEnabled(checked);
                Toast.makeText(this, "Animasi Antarmuka: " + (checked ? "Aktif" : "Mati"), Toast.LENGTH_SHORT).show();
            }
        });

        rgLens.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_lens_back) {
                settingsManager.setDefaultLens("Back");
            } else {
                settingsManager.setDefaultLens("Front");
            }
        });

        rgFps.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_30fps) {
                settingsManager.setFpsMode("30FPS");
                Toast.makeText(this, "Mode 30 FPS diaktifkan", Toast.LENGTH_SHORT).show();
            } else {
                settingsManager.setFpsMode("60FPS");
                Toast.makeText(this, "Mode 60 FPS diaktifkan", Toast.LENGTH_SHORT).show();
            }
        });

        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_theme_light) {
                settingsManager.setThemeMode("LIGHT");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.rb_theme_dark) {
                settingsManager.setThemeMode("DARK");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                settingsManager.setThemeMode("SYSTEM");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            Toast.makeText(this, "Tema diperbarui", Toast.LENGTH_SHORT).show();
        });

        rgAccent.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_accent_sage) {
                settingsManager.setAccentColor("Sage Green");
            } else if (checkedId == R.id.rb_accent_mocha) {
                settingsManager.setAccentColor("Vintage Mocha");
            } else {
                settingsManager.setAccentColor("Midnight Teal");
            }
            Toast.makeText(this, "Warna aksen disimpan", Toast.LENGTH_SHORT).show();
        });

        rgExportQuality.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_export_standard) {
                settingsManager.setExportQuality("Standard");
            } else if (checkedId == R.id.rb_export_max) {
                settingsManager.setExportQuality("Maximum");
            } else {
                settingsManager.setExportQuality("High");
            }
        });

        rgImageFormat.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_format_png) {
                settingsManager.setImageFormat("PNG");
            } else {
                settingsManager.setImageFormat("JPEG");
            }
        });

        btnSelectSaf.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            safLauncher.launch(intent);
        });

        btnReset.setOnClickListener(v -> confirmReset());
    }

    private void updateStorageText() {
        if ("SAF".equals(settingsManager.getStorageMode()) && !settingsManager.getSafUri().isEmpty()) {
            tvStoragePath.setText("Folder Kustom SAF: " + settingsManager.getSafUri());
        } else {
            tvStoragePath.setText(R.string.internal_storage);
        }
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Pengaturan")
                .setMessage("Kembalikan semua pengaturan ke default?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    settingsManager.resetAll();
                    loadSettings();
                    Toast.makeText(this, "Pengaturan telah direset", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}