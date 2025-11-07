// File: com/example/koicarehome_prm392/pond/AddEditPondActivity.java
package com.example.koicarehome_prm392;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.koicarehome_prm392.data.entities.Pond;
import com.example.koicarehome_prm392.ViewModels.PondViewModel;

public class AddEditPondActivity extends AppCompatActivity {

    // *** THÊM MỚI: Các hằng số để truyền dữ liệu qua Intent ***
    public static final String EXTRA_POND_ID = "com.example.koicarehome_prm392.EXTRA_POND_ID";
    public static final String EXTRA_POND_VOLUME = "com.example.koicarehome_prm392.EXTRA_POND_VOLUME";

    private EditText etPondVolume;
    private TextView tvVolume;
    private TextView tvFishCount;
    private Button btnSavePond;
    private Button btnCancel;
    private PondViewModel pondViewModel;
    private long currentUserId;

    private long pondIdToEdit = -1; // Biến để xác định chế độ Sửa. Mặc định là -1 (Thêm mới).

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_pond);

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_pond);
        setSupportActionBar(toolbar);

        etPondVolume = findViewById(R.id.etPondVolume);
        tvVolume = findViewById(R.id.tvVolume);
        tvFishCount = findViewById(R.id.tvFishCount);
        btnSavePond = findViewById(R.id.btnSavePond);
        btnCancel = findViewById(R.id.btnCancel);
        pondViewModel = new ViewModelProvider(this).get(PondViewModel.class);

        // Icon edit cho thể tích
        ImageView iconEditVolume = findViewById(R.id.iconEditVolume);
        iconEditVolume.setOnClickListener(v -> showEditVolumeDialog());

        // Icon edit cho danh sách cá
        ImageView iconEditFish = findViewById(R.id.iconEditFish);
        iconEditFish.setOnClickListener(v -> {
            if (pondIdToEdit != -1) {
                // Chuyển đến màn hình danh sách cá của hồ này
                Intent fishListIntent = new Intent(AddEditPondActivity.this, FishListActivity.class);
                fishListIntent.putExtra("POND_ID", pondIdToEdit);
                startActivity(fishListIntent);
            } else {
                Toast.makeText(this, "Vui lòng lưu hồ trước khi xem danh sách cá", Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
        currentUserId = prefs.getLong("current_user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_POND_ID)) {
            // Đây là chế độ Sửa
            setTitle("Sửa thông tin hồ");
            pondIdToEdit = intent.getLongExtra(EXTRA_POND_ID, -1);
            double volume = intent.getDoubleExtra(EXTRA_POND_VOLUME, 0);
            etPondVolume.setText(String.valueOf(volume));
            loadPondInfo();
        } else {
            setTitle("Thêm hồ mới");
            // Ở chế độ thêm mới, số cá = 0
            tvFishCount.setText("Tổng số cá: 0");
            // Tự động hiển thị dialog để nhập thể tích khi thêm mới
            showEditVolumeDialog();
        }

        // Tự động cập nhật thông tin khi nhập thể tích
        etPondVolume.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePondInfo();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSavePond.setOnClickListener(v -> savePond());
        btnCancel.setOnClickListener(v -> finish());

        // ---- Bottom Navigation ----
        android.view.View btnHome = findViewById(R.id.btnHome);
        android.view.View btnProfile = findViewById(R.id.btnProfile);

        btnHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(AddEditPondActivity.this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            Intent profileIntent = new Intent(AddEditPondActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        });
    }

    private void savePond() {
        String volumeStr = etPondVolume.getText().toString().trim();

        if (volumeStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thể tích hồ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double volume = Double.parseDouble(volumeStr);
            if (volume <= 0) {
                Toast.makeText(this, "Thể tích phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            double mineralAmount = volume * 0.003;
            long currentTime = System.currentTimeMillis();

            if (pondIdToEdit == -1) {
                Pond newPond = new Pond(currentUserId, volume, mineralAmount, currentTime);
                pondViewModel.insert(newPond);
                Toast.makeText(this, "Đã thêm hồ thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Pond updatedPond = new Pond(currentUserId, volume, mineralAmount, currentTime);
                updatedPond.pondId = pondIdToEdit;
                pondViewModel.update(updatedPond);
                Toast.makeText(this, "Đã cập nhật hồ thành công!", Toast.LENGTH_SHORT).show();
            }

            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Thể tích không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPondInfo() {
        if (pondIdToEdit != -1) {
            pondViewModel.getPondById(pondIdToEdit).observe(this, pond -> {
                if (pond != null) {
                    tvVolume.setText(String.format("Thể tích: %.0f L", pond.volumeLiters));
                    
                    // Đếm số cá trong hồ
                    new Thread(() -> {
                        com.example.koicarehome_prm392.data.db.AppDatabase db = 
                            com.example.koicarehome_prm392.data.db.AppDatabase.getInstance(getApplicationContext());
                        int fishCount = db.fishDao().getFishCountByPondId(pondIdToEdit);
                        runOnUiThread(() -> {
                            tvFishCount.setText("Tổng số cá: " + fishCount);
                        });
                    }).start();
                }
            });
        }
    }

    private void updatePondInfo() {
        String volumeStr = etPondVolume.getText().toString().trim();
        if (!volumeStr.isEmpty()) {
            try {
                double volume = Double.parseDouble(volumeStr);
                if (volume > 0) {
                    tvVolume.setText(String.format("Thể tích: %.0f L", volume));
                } else {
                    tvVolume.setText("Thể tích: - L");
                }
            } catch (NumberFormatException e) {
                tvVolume.setText("Thể tích: - L");
            }
        } else {
            tvVolume.setText("Thể tích: - L");
        }
    }

    private void showEditVolumeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa thể tích hồ");

        // Tạo EditText trong dialog
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Nhập thể tích (lít)");
        
        // Lấy giá trị hiện tại từ EditText
        String currentVolume = etPondVolume.getText().toString().trim();
        if (!currentVolume.isEmpty()) {
            input.setText(currentVolume);
        }
        
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String volumeStr = input.getText().toString().trim();
            if (!volumeStr.isEmpty()) {
                try {
                    double volume = Double.parseDouble(volumeStr);
                    if (volume > 0) {
                        etPondVolume.setText(volumeStr);
                        updatePondInfo();
                    } else {
                        Toast.makeText(this, "Thể tích phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Thể tích không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh số cá khi quay lại từ màn hình danh sách cá
        if (pondIdToEdit != -1) {
            new Thread(() -> {
                com.example.koicarehome_prm392.data.db.AppDatabase db = 
                    com.example.koicarehome_prm392.data.db.AppDatabase.getInstance(getApplicationContext());
                int fishCount = db.fishDao().getFishCountByPondId(pondIdToEdit);
                runOnUiThread(() -> {
                    tvFishCount.setText("Tổng số cá: " + fishCount);
                });
            }).start();
        }
    }
}