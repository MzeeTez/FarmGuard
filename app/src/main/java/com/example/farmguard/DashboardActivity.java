package com.example.farmguard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextView locationText;
    private Button startAssessmentButton;

    private final String SUPABASE_URL = BuildConfig.SUPABASE_URL;
    private final String SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY;
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeText = findViewById(R.id.welcome_text);
        locationText = findViewById(R.id.location_text);
        startAssessmentButton = findViewById(R.id.start_assessment_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Retrieve both email and access token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("FarmGuardPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("USER_EMAIL", null);
        String accessToken = sharedPreferences.getString("ACCESS_TOKEN", null);

        if (userEmail != null && accessToken != null) {
            // Fetch data using the retrieved credentials
            fetchUserData(userEmail, accessToken);
            fetchFarmData(accessToken);
        } else {
            // Handle case where user credentials are not found
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
        }

        startAssessmentButton.setOnClickListener(v -> Toast.makeText(this, "Start New Assessment Clicked", Toast.LENGTH_SHORT).show());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                return true; // Do nothing as we are on this screen
            } else if (itemId == R.id.nav_training) {
                Toast.makeText(this, "Training Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_compliance) {
                Toast.makeText(this, "Compliance Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_alerts) {
                Toast.makeText(this, "Alerts Clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
    }

    private void fetchUserData(String email, String accessToken) {
        // Query the users table for the specific email
        String url = SUPABASE_URL + "/rest/v1/users?select=name,role&email=eq." + email;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                // Use the user's specific access token for authentication
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> Toast.makeText(DashboardActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    mainHandler.post(() -> {
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);
                            if (jsonArray.length() > 0) {
                                JSONObject userObject = jsonArray.getJSONObject(0);
                                String name = userObject.getString("name");
                                String role = userObject.getString("role");
                                welcomeText.setText("Welcome, " + name + " (" + role + ")");
                            } else {
                                welcomeText.setText("Welcome, User!");
                            }
                        } catch (Exception e) {
                            Toast.makeText(DashboardActivity.this, "Error parsing user data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void fetchFarmData(String accessToken) {
        // You should create a security policy for the 'farms' table as well
        String url = SUPABASE_URL + "/rest/v1/farms?select=location&limit=1";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                // Use the user's access token here too for security
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> Toast.makeText(DashboardActivity.this, "Failed to fetch farm data.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    mainHandler.post(() -> {
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            if (jsonArray.length() > 0) {
                                JSONObject farmObject = jsonArray.getJSONObject(0);
                                String location = farmObject.getString("location");
                                locationText.setText(location);
                            }
                        } catch (Exception e) {
                            Toast.makeText(DashboardActivity.this, "Error parsing farm data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}