package com.example.farmguard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// Import the generated BuildConfig class to access your keys
import com.example.farmguard.BuildConfig;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerNowText;

    // Securely get the keys from BuildConfig
    private final String SUPABASE_URL = BuildConfig.SUPABASE_URL;
    private final String SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY;

    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        registerNowText = findViewById(R.id.register_now_text);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable the button to prevent multiple clicks
            loginButton.setEnabled(false);
            performLogin(email, password);
        });

        registerNowText.setOnClickListener(v -> {
            // Start the SignUpActivity when the text is clicked
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin(String email, String password) {
        // Construct the API endpoint URL for email/password login
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";

        // Create the JSON request body
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String jsonBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(jsonBody, JSON);

        // Build the HTTP request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .post(body)
                .build();

        // Execute the request asynchronously on a background thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Use a Handler to post UI updates back to the main thread
                mainHandler.post(() -> {
                    Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    loginButton.setEnabled(true); // Re-enable the button on failure
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();

                mainHandler.post(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Try to parse a more specific error message from the response
                        String errorMessage = "Login Failed";
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            errorMessage = jsonObject.optString("error_description", "Invalid credentials");
                        } catch (JSONException e) {
                            // Could not parse the error, use a generic message
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                    loginButton.setEnabled(true); // Re-enable the button after the request finishes
                });
            }
        });
    }
}

