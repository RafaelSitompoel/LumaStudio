package com.example.lumastudio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.lumastudio.utils.CustomFrameManager;
import com.example.lumastudio.utils.FramePreset;
import com.google.android.material.textfield.TextInputEditText;

public class FrameDesignerActivity extends BaseActivity {

    private ImageView imgFramePreview;
    private TextInputEditText etFrameName;
    private RadioGroup rgSlots, rgPattern;
    private CustomFrameManager customFrameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_designer);

        customFrameManager = new CustomFrameManager(this);

        initViews();
        updateLivePreview();
    }

    private void initViews() {
        imgFramePreview = findViewById(R.id.img_frame_preview);
        etFrameName = findViewById(R.id.et_frame_name);
        rgSlots = findViewById(R.id.rg_slots);
        rgPattern = findViewById(R.id.rg_pattern);

        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnSave = findViewById(R.id.btn_save_frame);
        Button btnSaveAction = findViewById(R.id.btn_save_frame_action);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveCustomFrame());
        btnSaveAction.setOnClickListener(v -> saveCustomFrame());

        rgSlots.setOnCheckedChangeListener((group, checkedId) -> updateLivePreview());
        rgPattern.setOnCheckedChangeListener((group, checkedId) -> updateLivePreview());

        etFrameName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLivePreview();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private int getSelectedSlotCount() {
        int checkedSlotId = rgSlots.getCheckedRadioButtonId();
        if (checkedSlotId == R.id.rb_2) return 2;
        if (checkedSlotId == R.id.rb_4) return 4;
        if (checkedSlotId == R.id.rb_6) return 6;
        return 3;
    }

    private String getSelectedPattern() {
        int checkedPatternId = rgPattern.getCheckedRadioButtonId();
        if (checkedPatternId == R.id.rb_pattern_coquette) return "Coquette Hearts";
        if (checkedPatternId == R.id.rb_pattern_retro) return "Vintage Retro";
        return "Korean Pastel";
    }

    private void updateLivePreview() {
        int slots = getSelectedSlotCount();
        String pattern = getSelectedPattern();

        int bgColor = Color.parseColor("#FAF6F0");
        int frameColor = Color.parseColor("#1D4542");

        if ("Coquette Hearts".equals(pattern)) {
            bgColor = Color.parseColor("#FDF2F5");
            frameColor = Color.parseColor("#E8A0BF");
        } else if ("Vintage Retro".equals(pattern)) {
            bgColor = Color.parseColor("#1A181B");
            frameColor = Color.parseColor("#D8B4F8");
        }

        Bitmap previewBmp = Bitmap.createBitmap(400, 600, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(previewBmp);
        canvas.drawColor(bgColor);

        Paint slotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        slotPaint.setColor(frameColor);

        int padding = 20;
        int usableHeight = 600 - (padding * (slots + 1)) - 40;
        int slotHeight = usableHeight / slots;
        int slotWidth = 400 - (padding * 2);

        for (int i = 0; i < slots; i++) {
            int top = padding + i * (slotHeight + padding);
            canvas.drawRect(padding, top, padding + slotWidth, top + slotHeight, slotPaint);
        }

        // Title at bottom
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#243B31"));
        textPaint.setTextSize(18f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        String name = etFrameName.getText() != null && !etFrameName.getText().toString().isEmpty()
                ? etFrameName.getText().toString() : "Custom Frame";
        canvas.drawText(name, 200, 580, textPaint);

        imgFramePreview.setImageBitmap(previewBmp);
    }

    private void saveCustomFrame() {
        String name = etFrameName.getText() != null && !etFrameName.getText().toString().isEmpty()
                ? etFrameName.getText().toString() : "Korean Aesthetic Frame";
        int slotCount = getSelectedSlotCount();
        String pattern = getSelectedPattern();

        int bgColor = Color.parseColor("#FAF6F0");
        int frameColor = Color.parseColor("#1D4542");
        if ("Coquette Hearts".equals(pattern)) {
            bgColor = Color.parseColor("#FDF2F5");
            frameColor = Color.parseColor("#E8A0BF");
        } else if ("Vintage Retro".equals(pattern)) {
            bgColor = Color.parseColor("#1A181B");
            frameColor = Color.parseColor("#D8B4F8");
        }

        FramePreset preset = new FramePreset(name, frameColor, bgColor, 44, 28, true, slotCount, pattern, 0);
        customFrameManager.savePreset(preset);

        Toast.makeText(this, "Bingkai kustom '" + name + "' berhasil disimpan!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("slot_count", slotCount);
        startActivity(intent);
        finish();
    }
}