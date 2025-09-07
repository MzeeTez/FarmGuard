package com.example.farmguard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class languageselectionactivity extends AppCompatActivity {

    private Button continueButton;
    private Map<RelativeLayout, ImageView> languageOptions;
    private String selectedLanguage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languageselectionactivity);

        continueButton = findViewById(R.id.continue_button);
        languageOptions = new HashMap<>();

        // Find views and map them
        languageOptions.put(
                findViewById(R.id.option_english),
                findViewById(R.id.checkmark_english)
        );
        languageOptions.put(
                findViewById(R.id.option_hindi),
                findViewById(R.id.checkmark_hindi)
        );
        languageOptions.put(
                findViewById(R.id.option_tamil),
                findViewById(R.id.checkmark_tamil)
        );
        languageOptions.put(
                findViewById(R.id.option_bengali),
                findViewById(R.id.checkmark_bengali)
        );

        // Set click listeners for each language option
        for (Map.Entry<RelativeLayout, ImageView> entry : languageOptions.entrySet()) {
            RelativeLayout layout = entry.getKey();
            layout.setOnClickListener(view -> handleLanguageSelection(layout));
        }

        continueButton.setOnClickListener(view -> {
            if (selectedLanguage != null) {
                Toast.makeText(languageselectionactivity.this, "Language selected: " + selectedLanguage, Toast.LENGTH_SHORT).show();
                // TODO: Add intent to go to the next activity (e.g., MainActivity)
            }
        });
    }

    private void handleLanguageSelection(RelativeLayout selectedLayout) {
        // Hide all checkmarks
        for (ImageView checkmark : languageOptions.values()) {
            checkmark.setVisibility(View.GONE);
        }

        // Show the checkmark for the selected layout
        ImageView selectedCheckmark = languageOptions.get(selectedLayout);
        if (selectedCheckmark != null) {
            selectedCheckmark.setVisibility(View.VISIBLE);
        }

        // Get the language name from the TextView inside the selected layout
        TextView textView = (TextView) selectedLayout.getChildAt(0);
        selectedLanguage = textView.getText().toString();

        // Enable the continue button
        continueButton.setEnabled(true);
    }
}