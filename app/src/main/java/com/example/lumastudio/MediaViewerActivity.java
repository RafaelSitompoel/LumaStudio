package com.example.lumastudio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lumastudio.utils.MediaShareManager;
import com.example.lumastudio.utils.StorageManager;

public class MediaViewerActivity extends AppCompatActivity {

    private ImageView imgViewer;
    private VideoView videoViewer;
    private ImageButton btnCompress;
    private TextView tvMediaTitle;

    private StorageManager storageManager;
    private Uri mediaUri;
    private boolean isVideo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_viewer);

        storageManager = new StorageManager(this);

        initViews();
        displayMedia();
    }

    private void initViews() {
        imgViewer = findViewById(R.id.img_viewer);
        videoViewer = findViewById(R.id.video_viewer);
        btnCompress = findViewById(R.id.btn_compress);
        tvMediaTitle = findViewById(R.id.tv_media_title);

        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnShare = findViewById(R.id.btn_share);
        ImageButton btnDelete = findViewById(R.id.btn_delete);

        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareMedia());
        btnDelete.setOnClickListener(v -> confirmDelete());

        btnCompress.setOnClickListener(v -> {
            Intent intent = new Intent(MediaViewerActivity.this, VideoCompressionActivity.class);
            intent.putExtra("video_uri", mediaUri.toString());
            startActivity(intent);
        });
    }

    private void displayMedia() {
        String uriStr = getIntent().getStringExtra("media_uri");
        isVideo = getIntent().getBooleanExtra("is_video", false);
        String name = getIntent().getStringExtra("media_name");

        if (uriStr == null) {
            finish();
            return;
        }

        mediaUri = Uri.parse(uriStr);
        if (name != null) tvMediaTitle.setText(name);

        if (isVideo) {
            imgViewer.setVisibility(View.GONE);
            videoViewer.setVisibility(View.VISIBLE);
            btnCompress.setVisibility(View.VISIBLE);

            MediaController controller = new MediaController(this);
            controller.setAnchorView(videoViewer);
            videoViewer.setMediaController(controller);
            videoViewer.setVideoURI(mediaUri);
            videoViewer.start();
        } else {
            videoViewer.setVisibility(View.GONE);
            btnCompress.setVisibility(View.GONE);
            imgViewer.setVisibility(View.VISIBLE);
            imgViewer.setImageURI(mediaUri);
        }
    }

    private void shareMedia() {
        if (mediaUri != null) {
            MediaShareManager.shareMedia(this, mediaUri, isVideo);
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Media")
                .setMessage("Apakah Anda yakin ingin menghapus file ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    if (storageManager.deleteMedia(mediaUri)) {
                        Toast.makeText(this, "File dihapus", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Gagal menghapus file", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}