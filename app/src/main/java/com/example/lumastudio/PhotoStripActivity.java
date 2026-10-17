package com.example.lumastudio;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lumastudio.models.FrameModel;
import com.example.lumastudio.utils.FrameCatalogManager;
import com.example.lumastudio.utils.FrameRenderer;
import com.example.lumastudio.utils.LumaSessionManager;
import com.example.lumastudio.utils.MediaShareManager;
import com.example.lumastudio.utils.StorageManager;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class PhotoStripActivity extends BaseActivity {

    private ImageView imgStripPreview;
    private MaterialButtonToggleGroup toggleLayout;
    private CheckBox chkShowDate;
    private EditText etCaption;
    private LinearLayout colorPaletteContainer;

    private StorageManager storageManager;
    private FrameRenderer.FrameParams frameParams;

    private final List<Bitmap> inputBitmaps = new ArrayList<>();
    private Bitmap currentStripBitmap;

    private final int[] PALETTE_COLORS = {
            Color.parseColor("#FAF6F0"), // Cream
            Color.parseColor("#FFFDF9"), // Warm White
            Color.parseColor("#F8E8EE"), // Soft Pink
            Color.parseColor("#E8E0F0"), // Lavender
            Color.parseColor("#D8B4F8"), // Pastel Purple
            Color.parseColor("#E8F0FE"), // Soft Sky
            Color.parseColor("#221F26"), // Dark Obsidian
            Color.parseColor("#1A181B")  // Deep Black
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_strip);

        storageManager = new StorageManager(this);
        frameParams = new FrameRenderer.FrameParams();

        initViews();
        loadInputPhotos();
        renderFramePresetsCarousel();
        renderColorPalette();
        updateStripPreview();
    }

    private void initViews() {
        imgStripPreview = findViewById(R.id.img_strip_preview);
        toggleLayout = findViewById(R.id.toggle_layout);
        chkShowDate = findViewById(R.id.chk_show_date);
        etCaption = findViewById(R.id.et_caption);
        colorPaletteContainer = findViewById(R.id.color_palette_container);

        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnSave = findViewById(R.id.btn_save);
        ImageButton btnShare = findViewById(R.id.btn_share);
        ImageButton btnPrint = findViewById(R.id.btn_print);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveStrip());
        btnShare.setOnClickListener(v -> shareStrip());
        if (btnPrint != null) {
            btnPrint.setOnClickListener(v -> com.example.lumastudio.utils.LumaPrintHelper.printBitmap(this, currentStripBitmap, "Luma Photostrip Print"));
        }

        toggleLayout.check(R.id.btn_vertical);
        toggleLayout.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                frameParams.isVertical = (checkedId == R.id.btn_vertical);
                updateStripPreview();
            }
        });

        chkShowDate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            frameParams.showDate = isChecked;
            updateStripPreview();
        });

        etCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                frameParams.captionText = s.toString();
                updateStripPreview();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadInputPhotos() {
        List<Bitmap> sessionPhotos = LumaSessionManager.getInstance().getSessionPhotos();
        if (sessionPhotos != null && !sessionPhotos.isEmpty()) {
            inputBitmaps.addAll(sessionPhotos);
        }

        // Fallback placeholder photos if empty
        if (inputBitmaps.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                Bitmap bmp = Bitmap.createBitmap(800, 1066, Bitmap.Config.ARGB_8888);
                bmp.eraseColor(i % 2 == 0 ? 0xFFE8E0F0 : 0xFFF8E8EE);
                inputBitmaps.add(bmp);
            }
        }
    }

    private void renderFramePresetsCarousel() {
        LinearLayout framePresetsContainer = findViewById(R.id.frame_presets_container);
        if (framePresetsContainer == null) return;
        framePresetsContainer.removeAllViews();

        List<FrameModel> frames = FrameCatalogManager.getAllFrames();
        for (FrameModel frame : frames) {
            Chip chip = new Chip(this);
            chip.setText(frame.getName());
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setFocusable(true);

            chip.setOnClickListener(v -> {
                frameParams.frameColor = frame.getFrameColor();
                frameParams.backgroundColor = frame.getBackgroundColor();
                frameParams.patternName = frame.getPatternName();
                updateStripPreview();
                Toast.makeText(this, "Bingkai diganti: " + frame.getName(), Toast.LENGTH_SHORT).show();
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 16, 0);
            chip.setLayoutParams(params);
            framePresetsContainer.addView(chip);
        }
    }

    private void renderColorPalette() {
        colorPaletteContainer.removeAllViews();
        for (int color : PALETTE_COLORS) {
            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(96, 96);
            params.setMargins(0, 0, 20, 0);
            card.setLayoutParams(params);
            card.setCardBackgroundColor(color);
            card.setRadius(48);
            card.setClickable(true);
            card.setFocusable(true);

            if (color == frameParams.frameColor) {
                card.setStrokeColor(getColor(R.color.luma_primary));
                card.setStrokeWidth(6);
            } else {
                card.setStrokeWidth(2);
                card.setStrokeColor(Color.LTGRAY);
            }

            card.setOnClickListener(v -> {
                frameParams.frameColor = color;
                renderColorPalette();
                updateStripPreview();
            });

            colorPaletteContainer.addView(card);
        }
    }

    private void updateStripPreview() {
        if (inputBitmaps.isEmpty()) return;
        currentStripBitmap = FrameRenderer.createPhotostrip(inputBitmaps, frameParams);
        imgStripPreview.setImageBitmap(currentStripBitmap);
    }

    private void saveStrip() {
        if (currentStripBitmap == null) return;
        com.example.lumastudio.utils.LumaLoadingDialog loading = new com.example.lumastudio.utils.LumaLoadingDialog(this, "Menyimpan Photostrip...");
        loading.show();

        new Thread(() -> {
            Uri uri = storageManager.saveBitmap(currentStripBitmap, "LUMA_STRIP");
            runOnUiThread(() -> {
                loading.dismiss();
                if (uri != null) {
                    Toast.makeText(this, R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void shareStrip() {
        if (currentStripBitmap == null) return;
        Uri uri = storageManager.saveBitmap(currentStripBitmap, "LUMA_STRIP_SHARE");
        if (uri != null) {
            MediaShareManager.shareMedia(this, uri, false);
        }
    }
}