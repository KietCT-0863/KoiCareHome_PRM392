package com.example.koicarehome_prm392;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.koicarehome_prm392.Adapters.FishAdapter;
import com.example.koicarehome_prm392.ViewModels.FishViewModel;
import com.example.koicarehome_prm392.data.entities.Fish;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FishListActivity extends AppCompatActivity implements FishAdapter.OnFishClickListener {
    private RecyclerView rvFishList;
    private FishAdapter adapter;
    private FishViewModel fishViewModel;
    private FloatingActionButton fabAddFish;
    private ActivityResultLauncher<Intent> addFishLauncher;
    private long pondId = -1; // -1 nghĩa là hiển thị tất cả cá

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_list);

        // Nhận POND_ID từ Intent nếu có
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("POND_ID")) {
            pondId = intent.getLongExtra("POND_ID", -1);
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupLauncher();
        observeData();
        setupBottomNavigation();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_fish_list);
        setSupportActionBar(toolbar);
        setTitle("Danh Sách Cá Koi");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_revert);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupBottomNavigation() {
        android.view.View btnHome = findViewById(R.id.btnHome);
        android.view.View btnProfile = findViewById(R.id.btnProfile);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(FishListActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FishListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        rvFishList = findViewById(R.id.rvFishList);
        fabAddFish = findViewById(R.id.fabAddFish);
        fishViewModel = new ViewModelProvider(this).get(FishViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new FishAdapter(this);
        rvFishList.setLayoutManager(new LinearLayoutManager(this));
        rvFishList.setAdapter(adapter);

        fabAddFish.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddFishActivity.class);
            if (pondId != -1) {
                intent.putExtra("POND_ID", pondId);
            }
            startActivity(intent);
        });
    }

    private void setupLauncher() {
        addFishLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Cập nhật danh sách cá...", Toast.LENGTH_SHORT).show();
                        // Không cần observe lại, Room LiveData sẽ tự cập nhật
                    }
                }
        );
    }

    private void observeData() {
        if (pondId != -1) {
            // Hiển thị cá của hồ cụ thể
            fishViewModel.getFishByPondId(pondId).observe(this, fishList -> {
                if (fishList != null) {
                    adapter.setFishList(fishList);
                }
            });
        } else {
            // Hiển thị tất cả cá
            fishViewModel.getAllFish().observe(this, fishList -> {
                if (fishList != null) {
                    adapter.setFishList(fishList);
                }
            });
        }
    }

    @Override
    public void onEditClick(Fish fish) {
        Intent intent = new Intent(this, AddFishActivity.class);
        intent.putExtra("FISH_ID", fish.fishId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại dữ liệu khi quay lại màn hình
        if (pondId != -1) {
            fishViewModel.getFishByPondId(pondId).observe(this, fishList -> {
                if (fishList != null) {
                    adapter.setFishList(fishList);
                }
            });
        } else {
            fishViewModel.getAllFish().observe(this, fishList -> {
                if (fishList != null) {
                    adapter.setFishList(fishList);
                }
            });
        }
    }

    @Override
    public void onDeleteClick(Fish fish) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa cá")
                .setMessage("Bạn có chắc muốn xóa con cá này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    fishViewModel.delete(fish);
                    Toast.makeText(this, "Đã xóa cá thành công", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

