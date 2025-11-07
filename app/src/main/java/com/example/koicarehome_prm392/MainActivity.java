package com.example.koicarehome_prm392;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koicarehome_prm392.Adapters.FishAdapter;
import com.example.koicarehome_prm392.Adapters.PondAdapter;
import com.example.koicarehome_prm392.ViewModels.FishViewModel;
import com.example.koicarehome_prm392.ViewModels.PondViewModel;
import com.example.koicarehome_prm392.data.db.AppDatabase;
import com.example.koicarehome_prm392.data.entities.Fish;
import com.example.koicarehome_prm392.data.entities.Pond;
import com.example.koicarehome_prm392.data.entities.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PondViewModel pondViewModel;
    private FishViewModel fishViewModel;
    private long currentUserId;
    private PondAdapter pondAdapter;
    private FishAdapter fishAdapter;
    private RecyclerView recyclerViewPonds;
    private RecyclerView recyclerViewFish;
    private FloatingActionButton fabAddPond;
    private boolean showingPonds = true; // true = đang hiển thị danh sách hồ, false = đang hiển thị danh sách cá

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
        currentUserId = prefs.getLong("current_user_id", -1);
        if (currentUserId == -1) {
            // Not logged in → redirect to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ---- Bottom Navigation ----
        android.view.View btnHome = findViewById(R.id.btnHome);
        android.view.View btnProfile = findViewById(R.id.btnProfile);

        btnHome.setOnClickListener(v -> {
            // Đã ở màn hình Home, không cần làm gì
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // ---- Setup click listeners cho các TextView ----
        TextView tvFishListTitle = findViewById(R.id.tvFishListTitle);
        TextView tvPondListTitle = findViewById(R.id.tvPondListTitle);
        
        tvFishListTitle.setOnClickListener(v -> showFishList());
        tvPondListTitle.setOnClickListener(v -> showPondList());

        // ---- Setup RecyclerView for Ponds and Fish ----
        setupRecyclerView();

        // ---- FloatingActionButton để thêm hồ mới ----
        fabAddPond = findViewById(R.id.fabAddPond);
        fabAddPond.setOnClickListener(v -> {
            if (showingPonds) {
                Intent intent = new Intent(MainActivity.this, AddEditPondActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, AddFishActivity.class);
                startActivity(intent);
            }
        });

        // ---- Initialize DB in background ----
        new Thread(() -> {
            try {
                Log.d("DB", "Attempting to initialize Room DB...");
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                // Trigger DB file creation
                User user = db.userDao().findById(1);
                Log.d("DB", "Room DB initialized and connection opened.");
            } catch (Exception e) {
                Log.e("DB", "DB init failed", e);
            }
        }).start();
    }

    private void setupRecyclerView() {
        // Setup RecyclerView cho hồ
        recyclerViewPonds = findViewById(R.id.recyclerViewPonds);
        recyclerViewPonds.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPonds.setHasFixedSize(true);

        pondAdapter = new PondAdapter();
        recyclerViewPonds.setAdapter(pondAdapter);

        // Xử lý click icon_edit để chuyển đến trang edit
        pondAdapter.setOnItemActionClickListener(new PondAdapter.OnItemActionClickListener() {
            @Override
            public void onEditClick(Pond pond) {
                Intent intent = new Intent(MainActivity.this, AddEditPondActivity.class);
                intent.putExtra(AddEditPondActivity.EXTRA_POND_ID, pond.pondId);
                intent.putExtra(AddEditPondActivity.EXTRA_POND_VOLUME, pond.volumeLiters);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Pond pond) {
                // Kiểm tra số lượng cá trong hồ trước khi xóa
                new Thread(() -> {
                    try {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                        int fishCount = db.fishDao().getFishCountByPondId(pond.pondId);
                        
                        runOnUiThread(() -> {
                            if (fishCount > 0) {
                                // Hồ còn cá, không cho phép xóa
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Không thể xóa hồ")
                                        .setMessage("Hồ số " + pond.pondId + " đang có " + fishCount + " con cá. " +
                                                "Vui lòng xóa hoặc di chuyển tất cả cá trước khi xóa hồ.")
                                        .setPositiveButton("Đồng ý", null)
                                        .show();
                            } else {
                                // Hồ không có cá, cho phép xóa
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Xác nhận xóa")
                                        .setMessage("Bạn có chắc chắn muốn xóa Hồ số: " + pond.pondId + "?")
                                        .setPositiveButton("Xóa", (dialog, which) -> {
                                            // Nếu người dùng chọn "Xóa", thì mới thực hiện xóa
                                            pondViewModel.delete(pond);
                                            Toast.makeText(MainActivity.this, "Đã xóa hồ", Toast.LENGTH_SHORT).show();
                                        })
                                        .setNegativeButton("Hủy", null) // Không làm gì nếu người dùng chọn "Hủy"
                                        .show();
                            }
                        });
                    } catch (Exception e) {
                        Log.e("MainActivity", "Error checking fish count", e);
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Lỗi kiểm tra thông tin hồ", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }
        });

        // Setup RecyclerView cho cá
        recyclerViewFish = findViewById(R.id.recyclerViewFish);
        recyclerViewFish.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFish.setHasFixedSize(true);

        fishAdapter = new FishAdapter(new FishAdapter.OnFishClickListener() {
            @Override
            public void onEditClick(Fish fish) {
                Intent intent = new Intent(MainActivity.this, AddFishActivity.class);
                intent.putExtra("FISH_ID", fish.fishId);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Fish fish) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Xóa cá")
                        .setMessage("Bạn có chắc muốn xóa con cá này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            fishViewModel.delete(fish);
                            Toast.makeText(MainActivity.this, "Đã xóa cá thành công", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        recyclerViewFish.setAdapter(fishAdapter);

        pondViewModel = new ViewModelProvider(this).get(PondViewModel.class);
        fishViewModel = new ViewModelProvider(this).get(FishViewModel.class);
        
        pondViewModel.getPondsByUserId(currentUserId).observe(this, ponds -> {
            pondAdapter.setPonds(ponds);
            // Tính và cập nhật số lượng cá và lượng thức ăn cho từng hồ
            updatePondInfo(ponds);
        });
        
        fishViewModel.getFishByUserId(currentUserId).observe(this, fishList -> {
            if (fishList != null) {
                fishAdapter.setFishList(fishList);
            }
        });
    }
    
    private void showFishList() {
        showingPonds = false;
        recyclerViewPonds.setVisibility(android.view.View.GONE);
        recyclerViewFish.setVisibility(android.view.View.VISIBLE);
        fabAddPond.setImageResource(android.R.drawable.ic_input_add);
    }
    
    private void showPondList() {
        showingPonds = true;
        recyclerViewPonds.setVisibility(android.view.View.VISIBLE);
        recyclerViewFish.setVisibility(android.view.View.GONE);
        fabAddPond.setImageResource(android.R.drawable.ic_input_add);
    }

    private void updatePondInfo(List<Pond> ponds) {
        // Tính số lượng cá và lượng thức ăn cho từng hồ trong background thread
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                for (Pond pond : ponds) {
                    int fishCount = db.fishDao().getFishCountByPondId(pond.pondId);
                    double foodAmount = db.fishDao().getTotalFoodAmountByPondId(pond.pondId);
                    runOnUiThread(() -> {
                        pondAdapter.setPondInfo(pond.pondId, fishCount, foodAmount);
                    });
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error updating pond info", e);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại thông tin từng hồ - lấy từ adapter
        if (pondAdapter != null) {
            // Lấy danh sách hồ hiện tại từ adapter và cập nhật thông tin
            pondViewModel.getPondsByUserId(currentUserId).observe(this, ponds -> {
                if (ponds != null && !ponds.isEmpty()) {
                    updatePondInfo(ponds);
                }
            });
        }
    }
}
