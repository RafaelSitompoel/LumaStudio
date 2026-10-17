package com.example.lumastudio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lumastudio.utils.FilterProcessor;
import com.example.lumastudio.utils.MediaShareManager;
import com.example.lumastudio.utils.StorageManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;

public class PhotoEditorActivity extends BaseActivity {

    private ImageView imgPreview;
    private Slider sliderIntensity;
    private LinearLayout layoutFiltersContainer;

    private StorageManager storageManager;
    private FilterProcessor.FilterParams filterParams;

    private Bitmap originalBitmap;
    private Bitmap currentFilteredBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);

        storageManager = new StorageManager(this);
        filterParams = new FilterProcessor.FilterParams();

        initViews();
        loadPhoto();
        renderFilterChips();
    }

    private void initViews() {
        imgPreview = findViewById(R.id.img_preview);
        sliderIntensity = findViewById(R.id.slider_intensity);
        layoutFiltersContainer = findViewById(R.id.layout_filters_container);

        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnSave = findViewById(R.id.btn_save);
        ImageButton btnShare = findViewById(R.id.btn_share);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> savePhoto());
        btnShare.setOnClickListener(v -> sharePhoto());

        sliderIntensity.addOnChangeListener((slider, value, fromUser) -> {
            filterParams.intensity = value / 100f;
            applyCurrentFilter();
        });
    }

    private void loadPhoto() {
        String photoPath = getIntent().getStringExtra("photo_path");
        if (photoPath != null && new java.io.File(photoPath).exists()) {
            originalBitmap = BitmapFactory.decodeFile(photoPath);
        } else {
            originalBitmap = Bitmap.createBitmap(800, 1066, Bitmap.Config.ARGB_8888);
            originalBitmap.eraseColor(0xFFE8E0F0);
        }
        currentFilteredBitmap = originalBitmap;
        imgPreview.setImageBitmap(currentFilteredBitmap);
    }

    private void renderFilterChips() {
        layoutFiltersContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < FilterProcessor.FILTER_NAMES.length; i++) {
            final int filterIndex = i;
            View chipView = inflater.inflate(R.layout.item_filter_chip, layoutFiltersContainer, false);
            TextView tvName = chipView.findViewById(R.id.tv_filter_name);
            MaterialCardView cardView = chipView.findViewById(R.id.card_filter);

            tvName.setText(FilterProcessor.FILTER_NAMES[i]);

            if (filterIndex == filterParams.presetIndex) {
                cardView.setStrokeColor(getColor(R.color.luma_primary));
                cardView.setStrokeWidth(6);
            } else {
                cardView.setStrokeWidth(0);
            }

            chipView.setOnClickListener(v -> {
                filterParams.presetIndex = filterIndex;
                renderFilterChips();
                applyCurrentFilter();
            });

            layoutFiltersContainer.addView(chipView);
        }
    }

    private void applyCurrentFilter() {
        if (originalBitmap == null) return;
        currentFilteredBitmap = FilterProcessor.applyFilter(originalBitmap, filterParams);
        imgPreview.setImageBitmap(currentFilteredBitmap);
    }

    private void savePhoto() {
        if (currentFilteredBitmap == null) return;
        com.example.lumastudio.utils.LumaLoadingDialog loading = new com.example.lumastudio.utils.LumaLoadingDialog(this, "Menyimpan Foto...");
        loading.show();

        new Thread(() -> {
            Uri uri = storageManager.saveBitmap(currentFilteredBitmap, "LUMA_EDIT");
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

    private void sharePhoto() {
        if (currentFilteredBitmap == null) return;
        Uri uri = storageManager.saveBitmap(currentFilteredBitmap, "LUMA_SHARE");
        if (uri != null) {
            MediaShareManager.shareMedia(this, uri, false);
        }
    }
}