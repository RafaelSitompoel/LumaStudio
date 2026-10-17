package com.example.lumastudio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.lumastudio.utils.LumaPrintHelper;
import com.example.lumastudio.utils.LumaSessionManager;

import java.io.InputStream;
import java.util.List;

public class PrintStudioActivity extends BaseActivity {

    private ImageView imgPrintPreview;
    private Bitmap currentPrintBitmap;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try (InputStream is = getContentResolver().openInputStream(uri)) {
                            currentPrintBitmap = BitmapFactory.decodeStream(is);
                            if (currentPrintBitmap != null) {
                                imgPrintPreview.setImageBitmap(currentPrintBitmap);
                                Toast.makeText(this, "Foto cetak dipilih!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Gagal memuat foto", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_studio);

        initViews();
        loadPrintPhoto();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        imgPrintPreview = findViewById(R.id.img_print_preview);
        Button btnSelectPhoto = findViewById(R.id.btn_select_photo);
        Button btnExecutePrint = findViewById(R.id.btn_execute_print);

        btnBack.setOnClickListener(v -> finish());

        btnSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });

        btnExecutePrint.setOnClickListener(v -> {
            if (currentPrintBitmap != null) {
                LumaPrintHelper.printBitmap(this, currentPrintBitmap, "Luma Photobooth Print");
            } else {
                Toast.makeText(this, "Pilih foto yang ingin dicetak terlebih dahulu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPrintPhoto() {
        List<Bitmap> sessionPhotos = LumaSessionManager.getInstance().getSessionPhotos();
        if (sessionPhotos != null && !sessionPhotos.isEmpty()) {
            currentPrintBitmap = sessionPhotos.get(0);
        } else {
            // Default sample print card
            currentPrintBitmap = Bitmap.createBitmap(800, 1200, Bitmap.Config.ARGB_8888);
            currentPrintBitmap.eraseColor(Color.parseColor("#F5F0E6"));
        }
        imgPrintPreview.setImageBitmap(currentPrintBitmap);
    }
}