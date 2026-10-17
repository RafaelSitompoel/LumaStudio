package com.example.lumastudio;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lumastudio.utils.StorageManager;
import com.example.lumastudio.utils.VideoCompressionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;

public class VideoCompressionActivity extends AppCompatActivity {

    private RadioGroup rgProfiles;
    private RadioButton rbSpaceSaver, rbBalanced, rbHighQuality;
    private ProgressBar pbCompress;
    private TextView tvStatus, tvSizeComparison;
    private Button btnStartCompress, btnSaveCompressed;

    private StorageManager storageManager;
    private VideoCompressionManager compressionManager;

    private File tempInputFile;
    private File compressedOutputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_compression);

        storageManager = new StorageManager(this);
        compressionManager = new VideoCompressionManager();

        initViews();
        prepareInputFile();
    }

    private void initViews() {
        rgProfiles = findViewById(R.id.rg_profiles);
        rbSpaceSaver = findViewById(R.id.rb_space_saver);
        rbBalanced = findViewById(R.id.rb_balanced);
        rbHighQuality = findViewById(R.id.rb_high_quality);
        pbCompress = findViewById(R.id.pb_compress);
        tvStatus = findViewById(R.id.tv_status);
        tvSizeComparison = findViewById(R.id.tv_size_comparison);
        btnStartCompress = findViewById(R.id.btn_start_compress);
        btnSaveCompressed = findViewById(R.id.btn_save_compressed);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        btnStartCompress.setOnClickListener(v -> startCompression());
        btnSaveCompressed.setOnClickListener(v -> saveCompressedVideo());
    }

    private void prepareInputFile() {
        String uriStr = getIntent().getStringExtra("video_uri");
        if (uriStr == null) {
            Toast.makeText(this, "Video tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Uri videoUri = Uri.parse(uriStr);
        tempInputFile = storageManager.createTempFile("LUMA_COMP_IN_", ".mp4");

        try (InputStream in = getContentResolver().openInputStream(videoUri);
             FileOutputStream out = new FileOutputStream(tempInputFile)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Gagal menyiapkan video", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startCompression() {
        if (tempInputFile == null || !tempInputFile.exists()) return;

        String profile = "BALANCED";
        if (rbSpaceSaver.isChecked()) profile = "SPACE_SAVER";
        else if (rbHighQuality.isChecked()) profile = "HIGH_QUALITY";

        compressedOutputFile = storageManager.createTempFile("LUMA_COMP_OUT_", ".mp4");

        btnStartCompress.setEnabled(false);
        rgProfiles.setEnabled(false);

        compressionManager.compressVideo(tempInputFile, compressedOutputFile, profile, new VideoCompressionManager.CompressionCallback() {
            @Override
            public void onStart() {
                tvStatus.setText("Mengompresi video...");
                pbCompress.setProgress(0);
            }

            @Override
            public void onProgress(int percent) {
                pbCompress.setProgress(percent);
                tvStatus.setText("Mengompresi video: " + percent + "%");
            }

            @Override
            public void onSuccess(File outputFile, long originalSizeBytes, long compressedSizeBytes) {
                tvStatus.setText("Kompresi Selesai!");
                pbCompress.setProgress(100);

                double origMB = originalSizeBytes / (1024.0 * 1024.0);
                double compMB = compressedSizeBytes / (1024.0 * 1024.0);
                double savedPercent = ((originalSizeBytes - compressedSizeBytes) * 100.0) / originalSizeBytes;

                String compText = String.format(
                        Locale.getDefault(),
                        "Ukuran Awal: %.1f MB  →  Hasil: %.1f MB (Hemat %.0f%%)",
                        origMB, compMB, Math.max(0, savedPercent)
                );

                tvSizeComparison.setText(compText);
                tvSizeComparison.setVisibility(View.VISIBLE);
                btnSaveCompressed.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                btnStartCompress.setEnabled(true);
                tvStatus.setText("Gagal mengompresi video");
                Toast.makeText(VideoCompressionActivity.this, "Gagal kompresi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCompressedVideo() {
        if (compressedOutputFile != null && compressedOutputFile.exists()) {
            Uri savedUri = storageManager.saveVideoFile(compressedOutputFile);
            if (savedUri != null) {
                Toast.makeText(this, R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compressionManager != null) {
            compressionManager.cancel();
        }
    }
}