package com.example.farmguard;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerNowText;

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
            loginButton.setEnabled(false);
            performLogin(email, password);
        });

        registerNowText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin(String email, String password) {
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String jsonBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> {
                    Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    loginButton.setEnabled(true);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();

                mainHandler.post(() -> {
                    if (response.isSuccessful()) {
                        try {
                            // On successful login, get the user's email AND access token
                            JSONObject jsonObject = new JSONObject(responseBody);
                            String accessToken = jsonObject.getString("access_token"); // Get the token
                            JSONObject userObject = jsonObject.getJSONObject("user");
                            String userEmail = userObject.getString("email");

                            // Save BOTH the email and the access token
                            SharedPreferences sharedPreferences = getSharedPreferences("FarmGuardPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("USER_EMAIL", userEmail);
                            editor.putString("ACCESS_TOKEN", accessToken); // Save the token
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Failed to parse login response.", Toast.LENGTH_LONG).show();
                            loginButton.setEnabled(true);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed: Invalid credentials", Toast.LENGTH_LONG).show();
                        loginButton.setEnabled(true);
                    }
                });
            }
        });
    }
}