package com.example.farmguard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import com.example.farmguard.BuildConfig;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, emailEditText, passwordEditText;
    private RadioGroup roleRadioGroup;
    private Button signUpButton;
    private TextView loginNowText;

    private final String SUPABASE_URL = BuildConfig.SUPABASE_URL;
    private final String SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY;
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.name_edit_text_signup);
        phoneEditText = findViewById(R.id.phone_edit_text_signup);
        emailEditText = findViewById(R.id.email_edit_text_signup);
        passwordEditText = findViewById(R.id.password_edit_text_signup);
        roleRadioGroup = findViewById(R.id.role_radio_group);
        signUpButton = findViewById(R.id.signup_button);
        loginNowText = findViewById(R.id.login_now_text);

        signUpButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || selectedRoleId == -1) {
                Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = findViewById(selectedRoleId);
            String role = selectedRadioButton.getText().toString().toLowerCase();

            signUpButton.setEnabled(false);
            performSignUp(name, phone, email, password, role);
        });

        loginNowText.setOnClickListener(v -> {
            finish();
        });
    }

    private void performSignUp(String name, String phone, String email, String password, String role) {
        String url = SUPABASE_URL + "/auth/v1/signup";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Create the nested 'data' object for your public.users table
        JSONObject dataObject = new JSONObject();
        try {
            dataObject.put("name", name);
            dataObject.put("phone", phone);
            dataObject.put("role", role);
        } catch (JSONException e) {
            e.printStackTrace();
            signUpButton.setEnabled(true);
            return;
        }

        // Create the main JSON object for the request
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("email", email);
            mainObject.put("password", password);
            mainObject.put("data", dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
            signUpButton.setEnabled(true);
            return;
        }

        RequestBody body = RequestBody.create(mainObject.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> {
                    Toast.makeText(SignUpActivity.this, "Sign-up Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    signUpButton.setEnabled(true);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();

                mainHandler.post(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Sign-up successful! Please check your email to verify your account.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String errorMessage = "Sign-up Failed";
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            errorMessage = jsonObject.optString("msg", "An error occurred. The email or phone may already be in use.");
                        } catch (JSONException e) {
                            // Could not parse error
                        }
                        Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                    signUpButton.setEnabled(true);
                });
            }
        });
    }
}

