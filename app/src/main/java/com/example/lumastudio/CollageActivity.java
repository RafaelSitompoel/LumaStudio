package com.example.lumastudio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lumastudio.utils.MediaItem;
import com.example.lumastudio.utils.StorageManager;

import java.util.ArrayList;
import java.util.List;

public class CollageActivity extends BaseActivity {

    private ImageView imgCollagePreview;
    private StorageManager storageManager;

    private final List<Bitmap> loadedBitmaps = new ArrayList<>();
    private Bitmap currentCollageBitmap;
    private int currentStyle = 1; // 1: 2x2, 2: 3-shot, 3: Polaroid Wall

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage);

        storageManager = new StorageManager(this);

        initViews();
        loadSourcePhotos();
        generateCollage();
    }

    private void initViews() {
        imgCollagePreview = findViewById(R.id.img_collage_preview);
        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnSave = findViewById(R.id.btn_save);

        Button btnStyle2x2 = findViewById(R.id.btn_style_2x2);
        Button btnStyle3Shot = findViewById(R.id.btn_style_3shot);
        Button btnStylePolaroid = findViewById(R.id.btn_style_polaroid);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveCollage());

        btnStyle2x2.setOnClickListener(v -> {
            currentStyle = 1;
            generateCollage();
        });

        btnStyle3Shot.setOnClickListener(v -> {
            currentStyle = 2;
            generateCollage();
        });

        btnStylePolaroid.setOnClickListener(v -> {
            currentStyle = 3;
            generateCollage();
        });
    }

    private void loadSourcePhotos() {
        List<MediaItem> mediaItems = storageManager.loadLumaMedia();
        for (int i = 0; i < Math.min(4, mediaItems.size()); i++) {
            try {
                Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(mediaItems.get(i).getUri()));
                if (bmp != null) loadedBitmaps.add(bmp);
            } catch (Exception ignored) {
            }
        }

        // Fallback placeholders if not enough media
        while (loadedBitmaps.size() < 4) {
            Bitmap placeholder = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
            placeholder.eraseColor(loadedBitmaps.size() % 2 == 0 ? Color.parseColor("#E8E0F0") : Color.parseColor("#F8E8EE"));
            loadedBitmaps.add(placeholder);
        }
    }

    private void generateCollage() {
        int width = 1080;
        int height = 1080;
        Bitmap collage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(collage);
        canvas.drawColor(Color.parseColor("#FAF6F0"));

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        if (currentStyle == 1) { // 2x2 Grid
            int halfW = width / 2;
            int halfH = height / 2;
            int padding = 16;

            if (loadedBitmaps.size() >= 4) {
                canvas.drawBitmap(Bitmap.createScaledBitmap(loadedBitmaps.get(0), halfW - padding, halfH - padding, true), padding, padding, paint);
                canvas.drawBitmap(Bitmap.createScaledBitmap(loadedBitmaps.get(1), halfW - padding, halfH - padding, true), halfW + padding / 2f, padding, paint);
                canvas.drawBitmap(Bitmap.createScaledBitmap(loadedBitmaps.get(2), halfW - padding, halfH - padding, true), padding, halfH + padding / 2f, paint);
                canvas.drawBitmap(Bitmap.createScaledBitmap(loadedBitmaps.get(3), halfW - padding, halfH - padding, true), halfW + padding / 2f, halfH + padding / 2f, paint);
            }
        } else { // 3-Shot Layout
            int thirdH = height / 3;
            int padding = 16;
            for (int i = 0; i < 3 && i < loadedBitmaps.size(); i++) {
                Bitmap scaled = Bitmap.createScaledBitmap(loadedBitmaps.get(i), width - (padding * 2), thirdH - (padding * 2), true);
                canvas.drawBitmap(scaled, padding, padding + i * thirdH, paint);
            }
        }

        currentCollageBitmap = collage;
        imgCollagePreview.setImageBitmap(currentCollageBitmap);
    }

    private void saveCollage() {
        if (currentCollageBitmap == null) return;
        Uri uri = storageManager.saveBitmap(currentCollageBitmap, "LUMA_COLLAGE");
        if (uri != null) {
            Toast.makeText(this, R.string.saved_successfully, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
        }
    }
}