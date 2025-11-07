package com.example.koicarehome_prm392;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.koicarehome_prm392.data.db.AppDatabase;
import com.example.koicarehome_prm392.data.entities.User;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserNameLabel;
    private TextView tvUserName;
    private AppCompatButton btnLogout;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
        currentUserId = prefs.getLong("current_user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadUserInfo();
        setupListeners();
    }

    private void initViews() {
        tvUserNameLabel = findViewById(R.id.tvUserNameLabel);
        tvUserName = findViewById(R.id.tvUserName);
        btnLogout = findViewById(R.id.btnLogout);
        
        // Set màu xanh dương cho button bằng code để đảm bảo không bị override
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(ContextCompat.getColor(this, R.color.water_blue));
        drawable.setCornerRadius(12 * getResources().getDisplayMetrics().density); // 12dp
        btnLogout.setBackground(drawable);
        btnLogout.setBackgroundTintList(null);
    }

    private void loadUserInfo() {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                User user = db.userDao().findById(currentUserId);
                if (user != null) {
                    runOnUiThread(() -> {
                        tvUserNameLabel.setText("Tên người dùng:");
                        tvUserName.setText(user.userName);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("current_user_id");
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Bottom Navigation
        android.view.View btnHome = findViewById(R.id.btnHome);
        android.view.View btnProfile = findViewById(R.id.btnProfile);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            // Đã ở màn hình Profile, không cần làm gì
        });
    }
}

