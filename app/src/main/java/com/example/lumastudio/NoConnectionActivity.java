package com.example.lumastudio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.lumastudio.utils.NetworkManager;

public class NoConnectionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);

        Button btnRetry = findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(v -> {
            btnRetry.setEnabled(false);
            Toast.makeText(this, "Memeriksa koneksi internet...", Toast.LENGTH_SHORT).show();
            NetworkManager.checkConnection(this, isConnected -> {
                btnRetry.setEnabled(true);
                if (isConnected) {
                    Toast.makeText(this, "Koneksi terhubung!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NoConnectionActivity.this, SplashActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Masih tidak ada koneksi internet. Nyalakan Wi-Fi atau Data Seluler.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}