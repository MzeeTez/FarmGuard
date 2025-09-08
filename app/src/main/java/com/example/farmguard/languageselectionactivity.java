package com.example.farmguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class languageselectionactivity extends AppCompatActivity {

    private Button continueButton;
    private Map<Integer, String> viewIdToLangCodeMap;
    private Map<String, Integer> langCodeToViewIdMap;
    private Map<RelativeLayout, ImageView> languageOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languageselectionactivity);

        initializeViews();
        initializeLanguageMaps();
        setupClickListeners();
        updateUIBasedOnCurrentLocale();

        continueButton.setOnClickListener(view -> {
            // When continue is clicked, go to LoginActivity
            Intent intent = new Intent(languageselectionactivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initializeViews() {
        continueButton = findViewById(R.id.continue_button);
        languageOptions = new HashMap<>();
        languageOptions.put(findViewById(R.id.option_english), findViewById(R.id.checkmark_english));
        languageOptions.put(findViewById(R.id.option_hindi), findViewById(R.id.checkmark_hindi));
        languageOptions.put(findViewById(R.id.option_tamil), findViewById(R.id.checkmark_tamil));
        languageOptions.put(findViewById(R.id.option_bengali), findViewById(R.id.checkmark_bengali));
    }

    private void initializeLanguageMaps() {
        viewIdToLangCodeMap = new HashMap<>();
        viewIdToLangCodeMap.put(R.id.option_english, "en");
        viewIdToLangCodeMap.put(R.id.option_hindi, "hi");
        viewIdToLangCodeMap.put(R.id.option_tamil, "ta");
        viewIdToLangCodeMap.put(R.id.option_bengali, "bn");

        langCodeToViewIdMap = new HashMap<>();
        langCodeToViewIdMap.put("en", R.id.option_english);
        langCodeToViewIdMap.put("hi", R.id.option_hindi);
        langCodeToViewIdMap.put("ta", R.id.option_tamil);
        langCodeToViewIdMap.put("bn", R.id.option_bengali);
    }

    private void setupClickListeners() {
        for (RelativeLayout layout : languageOptions.keySet()) {
            layout.setOnClickListener(view -> {
                String langCode = viewIdToLangCodeMap.get(view.getId());
                if (langCode != null && !Locale.getDefault().getLanguage().equals(langCode)) {
                    LocaleHelper.setLocale(this, langCode);
                    recreate(); // This will restart the activity and apply the language
                }
            });
        }
    }

    private void updateUIBasedOnCurrentLocale() {
        // Get the current language of the app
        String currentLangCode = Locale.getDefault().getLanguage();
        Integer selectedViewId = langCodeToViewIdMap.get(currentLangCode);

        if (selectedViewId != null) {
            // Find the corresponding layout and checkmark
            RelativeLayout selectedLayout = findViewById(selectedViewId);
            ImageView selectedCheckmark = languageOptions.get(selectedLayout);

            if (selectedCheckmark != null) {
                selectedCheckmark.setVisibility(View.VISIBLE);
                continueButton.setEnabled(true);
            }
        }
    }
}