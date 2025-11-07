<<<<<<< HEAD
=======
// Đường dẫn file: D:/PRM392/KoiCareHome_PRM392/app/src/main/java/com/example/koicarehome_prm392/MainActivity.java
>>>>>>> origin/main
package com.example.koicarehome_prm392;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
<<<<<<< HEAD
import android.widget.Button;
=======
import android.widget.Button; // *** THÊM MỚI ***
>>>>>>> origin/main

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.koicarehome_prm392.data.db.AppDatabase;
import com.example.koicarehome_prm392.data.entities.User;
<<<<<<< HEAD
=======
import com.example.koicarehome_prm392.pond.PondListActivity;
>>>>>>> origin/main

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
        long currentUserId = prefs.getLong("current_user_id", -1);
        if (currentUserId == -1) {
            // not logged in — go to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
=======

>>>>>>> origin/main
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

<<<<<<< HEAD
        Button btnFishList = findViewById(R.id.btnFishList);
        btnFishList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FishListActivity.class);
=======
        Button btnGoToPonds = findViewById(R.id.btn_go_to_ponds);

        btnGoToPonds.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PondListActivity.class);
>>>>>>> origin/main
            startActivity(intent);
        });

        new Thread(() -> {
            try {
                Log.d("DB", "Attempting to initialize Room DB...");
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                // Run any query to force the .db file to be created.
                User user = db.userDao().findById(1);
                Log.d("DB", "Room DB initialized and connection opened.");
            } catch (Exception e) {
                Log.e("DB", "DB init failed", e);
            }
        }).start();
    }
}