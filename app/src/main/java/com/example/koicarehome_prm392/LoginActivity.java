package com.example.koicarehome_prm392;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.koicarehome_prm392.data.db.AppDatabase;
import com.example.koicarehome_prm392.data.entities.User;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    EditText etUser, etPass;
    AppCompatButton btnLogin, btnGoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);
        
        // Set màu xanh dương cho các button
        setupButtonColor(btnLogin);
        setupButtonColor(btnGoRegister);

        btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            final String username = etUser.getText().toString().trim();
            final String password = etPass.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username & password", Toast.LENGTH_SHORT).show();
                return;
            }

            // run DB ops off main thread
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                User u = db.userDao().findByName(username);
                if (u == null) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show());
                    return;
                }

                String hash = HashUtils.sha256(password);
                if (u.passwordHash.equals(hash)) {
                    // success -> save current_user_id and open MainActivity
                    SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
                    prefs.edit().putLong("current_user_id", u.userId).apply();

                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
    
    private void setupButtonColor(AppCompatButton button) {
        // Set màu xanh dương cho button bằng code để đảm bảo không bị override
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(ContextCompat.getColor(this, R.color.water_blue));
        drawable.setCornerRadius(12 * getResources().getDisplayMetrics().density); // 12dp
        button.setBackground(drawable);
        button.setBackgroundTintList(null);
    }
}