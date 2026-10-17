package com.example.lumastudio;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lumastudio.utils.PoseAssistantManager;
import com.example.lumastudio.utils.SoundManager;

public class PoseAssistantActivity extends AppCompatActivity {

    private TextView tvPoseTitle, tvPoseHint, tvCountdownDisplay;
    private Button btnStartAssistant;
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose_assistant);

        initViews();
    }

    private void initViews() {
        tvPoseTitle = findViewById(R.id.tv_pose_title);
        tvPoseHint = findViewById(R.id.tv_pose_hint);
        tvCountdownDisplay = findViewById(R.id.tv_countdown_display);
        btnStartAssistant = findViewById(R.id.btn_start_assistant);

        ImageButton btnBack = findViewById(R.id.btn_back);
        Button btnHeart = findViewById(R.id.btn_pose_heart);
        Button btnWink = findViewById(R.id.btn_pose_wink);
        Button btnFunny = findViewById(R.id.btn_pose_funny);

        btnBack.setOnClickListener(v -> finish());

        btnHeart.setOnClickListener(v -> setPoseTheme("Heart Cheek", "Letakkan jari telunjuk di pipi membentuk hati kecil. Senyum manis!"));
        btnWink.setOnClickListener(v -> setPoseTheme("Cool Wink", "Kedipkan satu mata ke arah kamera dan miringkan kepala sedikit."));
        btnFunny.setOnClickListener(v -> setPoseTheme("Funny Face", "Buat ekspresi lucu atau konyol yang paling seru bersama teman!"));

        btnStartAssistant.setOnClickListener(v -> {
            if (!isRunning) startPoseSession();
        });
    }

    private void setPoseTheme(String title, String hint) {
        if (isRunning) return;
        tvPoseTitle.setText(title);
        tvPoseHint.setText(hint);
    }

    private void startPoseSession() {
        isRunning = true;
        btnStartAssistant.setEnabled(false);
        tvCountdownDisplay.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sec = (millisUntilFinished / 1000) + 1;
                tvCountdownDisplay.setText(String.valueOf(sec));
                tvPoseHint.setText(PoseAssistantManager.getHintForShot((int) (3 - sec), 3));
                SoundManager.getInstance(PoseAssistantActivity.this).playShutterClick();
            }

            @Override
            public void onFinish() {
                tvCountdownDisplay.setText("✨ Selesai!");
                tvPoseHint.setText("Hebat sekali posenya! Foto berhasil diambil.");
                SoundManager.getInstance(PoseAssistantActivity.this).playShutterClick();
                isRunning = false;
                btnStartAssistant.setEnabled(true);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}