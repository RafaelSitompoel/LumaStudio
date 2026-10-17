package com.example.lumastudio;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lumastudio.utils.SettingsManager;

public abstract class BaseActivity extends AppCompatActivity {

    protected SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settingsManager = new SettingsManager(this);
        String accent = settingsManager.getAccentColor();

        if ("Sage Green".equals(accent)) {
            setTheme(R.style.Theme_LumaStudio_SageGreen);
        } else if ("Vintage Mocha".equals(accent)) {
            setTheme(R.style.Theme_LumaStudio_VintageMocha);
        } else if ("Slate Blue".equals(accent)) {
            setTheme(R.style.Theme_LumaStudio_SlateBlue);
        } else if ("Classic Charcoal".equals(accent)) {
            setTheme(R.style.Theme_LumaStudio_ClassicCharcoal);
        } else if ("Forest Green".equals(accent)) {
            setTheme(R.style.Theme_LumaStudio_ForestGreen);
        } else {
            setTheme(R.style.Theme_LumaStudio_MidnightTeal);
        }

        super.onCreate(savedInstanceState);

        if (settingsManager.isAnimationEnabled()) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (settingsManager != null && settingsManager.isAnimationEnabled()) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}