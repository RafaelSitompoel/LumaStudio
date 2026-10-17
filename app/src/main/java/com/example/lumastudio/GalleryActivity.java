package com.example.lumastudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumastudio.utils.MediaItem;
import com.example.lumastudio.utils.StorageManager;

import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView rvGallery;
    private LinearLayout layoutEmptyState;

    private StorageManager storageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        storageManager = new StorageManager(this);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMediaList();
    }

    private void initViews() {
        rvGallery = findViewById(R.id.rv_gallery);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        ImageButton btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        rvGallery.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void loadMediaList() {
        List<MediaItem> items = storageManager.loadLumaMedia();

        if (items.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvGallery.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvGallery.setVisibility(View.VISIBLE);

            GalleryAdapter adapter = new GalleryAdapter(this, items, item -> {
                Intent intent = new Intent(GalleryActivity.this, MediaViewerActivity.class);
                intent.putExtra("media_uri", item.getUri().toString());
                intent.putExtra("is_video", item.isVideo());
                intent.putExtra("media_name", item.getName());
                intent.putExtra("media_size", item.getSize());
                startActivity(intent);
            });
            rvGallery.setAdapter(adapter);
        }
    }
}