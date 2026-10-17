package com.example.lumastudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
    }

    private void initViews() {
        MaterialCardView cardStartBooth = findViewById(R.id.card_start_booth);
        Button btnBukaKamera = findViewById(R.id.btn_buka_kamera);
        MaterialCardView cardFrameGallery = findViewById(R.id.card_frame_gallery);
        MaterialCardView cardCreativeStudio = findViewById(R.id.card_creative_studio);
        MaterialCardView cardAlbum = findViewById(R.id.card_album);
        MaterialCardView cardFrameDesigner = findViewById(R.id.card_frame_designer);
        MaterialCardView cardAiStudio = findViewById(R.id.card_ai_studio);
        MaterialCardView cardPrintStudio = findViewById(R.id.card_print_studio);
        ImageButton btnSettings = findViewById(R.id.btn_settings);
        Button btnAbout = findViewById(R.id.btn_about);

        View.OnClickListener cameraListener = v -> startActivity(new Intent(HomeActivity.this, MainActivity.class));

        cardStartBooth.setOnClickListener(cameraListener);
        btnBukaKamera.setOnClickListener(cameraListener);

        cardFrameGallery.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FrameGalleryActivity.class)));
        cardCreativeStudio.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PhotoEditorActivity.class)));
        cardAlbum.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, GalleryActivity.class)));
        cardFrameDesigner.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FrameDesignerActivity.class)));
        cardAiStudio.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AiStudioActivity.class)));
        cardPrintStudio.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PrintStudioActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SettingsActivity.class)));
        btnAbout.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AboutActivity.class)));
    }
}