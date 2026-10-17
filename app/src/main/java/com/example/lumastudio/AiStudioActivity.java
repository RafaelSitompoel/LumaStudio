package com.example.lumastudio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.lumastudio.utils.LumaLoadingDialog;

import java.io.InputStream;

public class AiStudioActivity extends BaseActivity {

    private ImageView imgInputPhoto, imgAiResult;
    private TextView tvAiCaptionResult;
    private RadioGroup rgAiStyle;

    private Bitmap inputBitmap;
    private Bitmap transformedBitmap;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try (InputStream is = getContentResolver().openInputStream(uri)) {
                            inputBitmap = BitmapFactory.decodeStream(is);
                            if (inputBitmap != null) {
                                imgInputPhoto.setImageBitmap(inputBitmap);
                                Toast.makeText(this, "Foto sumber dipilih!", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_ai_studio);

        initViews();
        loadDefaultSamplePhoto();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        imgInputPhoto = findViewById(R.id.img_input_photo);
        imgAiResult = findViewById(R.id.img_ai_result);
        tvAiCaptionResult = findViewById(R.id.tv_ai_caption_result);
        rgAiStyle = findViewById(R.id.rg_ai_style);
        Button btnSelectPhoto = findViewById(R.id.btn_select_photo);
        Button btnGenerate = findViewById(R.id.btn_generate_ai);

        btnBack.setOnClickListener(v -> finish());

        btnSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });

        btnGenerate.setOnClickListener(v -> generateAiPhotoArt());
    }

    private void loadDefaultSamplePhoto() {
        inputBitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(inputBitmap);
        canvas.drawColor(Color.parseColor("#1D4542"));

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#B8E0D2"));
        paint.setTextSize(48f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("LUMA PHOTO SAMPLE", 400, 420, paint);

        imgInputPhoto.setImageBitmap(inputBitmap);
    }

    private void generateAiPhotoArt() {
        if (inputBitmap == null) return;

        LumaLoadingDialog loading = new LumaLoadingDialog(this, "Generating AI Photo Transformation & Art...");
        loading.show();

        int selectedStyleId = rgAiStyle.getCheckedRadioButtonId();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loading.dismiss();

            String styleName;
            ColorMatrix colorMatrix = new ColorMatrix();

            if (selectedStyleId == R.id.rb_style_anime) {
                styleName = "Soft Anime Glow";
                // Bright soft pastel saturation
                colorMatrix.setSaturation(1.6f);
            } else if (selectedStyleId == R.id.rb_style_vintage) {
                styleName = "Vintage Film 90s";
                // Sepia retro warm matrix
                colorMatrix.set(new float[]{
                        0.393f, 0.769f, 0.189f, 0, 0,
                        0.349f, 0.686f, 0.168f, 0, 0,
                        0.272f, 0.534f, 0.131f, 0, 0,
                        0, 0, 0, 1, 0
                });
            } else if (selectedStyleId == R.id.rb_style_golden) {
                styleName = "Kodak Golden Hour";
                // Warm golden yellow tint
                colorMatrix.set(new float[]{
                        1.2f, 0.1f, 0, 0, 15,
                        0, 1.1f, 0, 0, 10,
                        0, 0, 0.8f, 0, -10,
                        0, 0, 0, 1, 0
                });
            } else {
                styleName = "Cyberpunk Neon Vibe";
                // High contrast cyan/magenta boost
                colorMatrix.set(new float[]{
                        1.4f, -0.2f, 0.2f, 0, 20,
                        -0.1f, 1.3f, 0.3f, 0, -10,
                        0.4f, 0.1f, 1.6f, 0, 40,
                        0, 0, 0, 1, 0
                });
            }

            transformedBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(transformedBitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(inputBitmap, 0, 0, paint);

            imgAiResult.setImageBitmap(transformedBitmap);
            tvAiCaptionResult.setText("✨ AI Style: " + styleName + "\n\"Captured moments transformed into timeless " + styleName + " aesthetic art. #LumaAIStudio\"");

            Toast.makeText(this, "Karya foto AI berhasil dibuat!", Toast.LENGTH_SHORT).show();
        }, 1800);
    }
}