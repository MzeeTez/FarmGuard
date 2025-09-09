package com.example.farmguard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply saved language preference
        SharedPreferences sharedPreferences = getSharedPreferences("FarmGuardPrefs", MODE_PRIVATE);
        String languageCode = sharedPreferences.getString("SELECTED_LANGUAGE", "en");
        LocaleHelper.setLocale(this, languageCode);

        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false);
            String userName = sharedPreferences.getString("USER_NAME", "");

            Intent i;
            if (isLoggedIn) {
                i = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                i.putExtra("USER_NAME", userName);
            } else {
                i = new Intent(SplashScreenActivity.this, languageselectionactivity.class);
            }
            startActivity(i);
            finish();
        }, SPLASH_TIMEOUT);
    }
}