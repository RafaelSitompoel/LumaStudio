package com.example.lumastudio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

public class AiAssistantActivity extends BaseActivity {

    private TextView tvAiOutput;
    private final Random random = new Random();

    private final String[] AI_CAPTIONS = {
            "✨ Captured moments with my favorite person. #LumaBooth",
            "🌸 Healing day with good vibes and timeless memories.",
            "☕ Coffee, camera & aesthetic analog memories.",
            "💫 Retro nights, warm smiles, and midnight teal dreams.",
            "🎞️ Film grain, soft glow, and unforgettable laughter."
    };

    private final String[] AI_FILTERS = {
            "🎨 Rekomendasi AI Vibe: 'Vintage Film' dengan Grain 40% & Vignette tipis untuk kesan sinematik.",
            "🎨 Rekomendasi AI Vibe: 'Kodak Warm' dengan Temperatur +15 dan Contrast sedang.",
            "🎨 Rekomendasi AI Vibe: 'Coquette Soft Pink' dengan Brightness hangat.",
            "🎨 Rekomendasi AI Vibe: 'Midnight Teal Look' dengan Cool Blue tone."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        initViews();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        Button btnCaption = findViewById(R.id.btn_generate_caption);
        Button btnFilter = findViewById(R.id.btn_recommend_filter);
        tvAiOutput = findViewById(R.id.tv_ai_output);

        btnBack.setOnClickListener(v -> finish());

        btnCaption.setOnClickListener(v -> {
            String caption = AI_CAPTIONS[random.nextInt(AI_CAPTIONS.length)];
            tvAiOutput.setText(caption);
        });

        btnFilter.setOnClickListener(v -> {
            String filter = AI_FILTERS[random.nextInt(AI_FILTERS.length)];
            tvAiOutput.setText(filter);
        });
    }
}