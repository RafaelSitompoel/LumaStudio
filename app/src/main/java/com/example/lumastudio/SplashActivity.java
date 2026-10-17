package com.example.lumastudio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LinearLayout logoContainer = findViewById(R.id.logo_container);

        // Scale & Fade Animation
        ScaleAnimation animation = new ScaleAnimation(
                0.8f, 1.0f, 0.8f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setDuration(1000);
        animation.setFillAfter(true);
        logoContainer.startAnimation(animation);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }, 1600);
    }
}