package com.example.koicarehome_prm392;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.koicarehome_prm392.ViewModels.FishViewModel;
import com.example.koicarehome_prm392.ViewModels.PondViewModel;
import com.example.koicarehome_prm392.data.entities.Fish;
import com.example.koicarehome_prm392.data.entities.Pond;

import java.util.ArrayList;
import java.util.List;

public class AddFishActivity extends AppCompatActivity {
    private EditText etFishName, etFishColor, etLength, etWeight;
    private ImageView ivFishPreview;
    private Button btnSelectImage, btnSaveFish;
    private TextView tvFoodAmount, tvSelectedPond;
    private Spinner spinnerPond;
    private LinearLayout llPondSelection;

    private FishViewModel fishViewModel;
    private PondViewModel pondViewModel;
    private List<Pond> pondList = new ArrayList<>();
    private String selectedImageUri = "";
    private long editFishId = -1;
    private long preSelectedPondId = -1; // POND_ID được truyền từ Intent

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fish);

        initViews();
        initViewModels();
        setupImagePicker();
        setupListeners();
        
        // Kiểm tra xem có POND_ID được truyền từ Intent không
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("POND_ID")) {
            preSelectedPondId = intent.getLongExtra("POND_ID", -1);
        }
        
        loadPonds();

        // Check if editing
        if (intent != null && intent.hasExtra("FISH_ID")) {
            editFishId = intent.getLongExtra("FISH_ID", -1);
            loadFishData(editFishId);
        }
        
        // Cấu hình hiển thị spinner hoặc TextView dựa trên preSelectedPondId
        setupPondSelection();
    }

    private void initViews() {
        etFishName = findViewById(R.id.etFishName);
        etFishColor = findViewById(R.id.etFishColor);
        etLength = findViewById(R.id.etLength);
        etWeight = findViewById(R.id.etWeight);
        ivFishPreview = findViewById(R.id.ivFishPreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSaveFish = findViewById(R.id.btnSaveFish);
        tvFoodAmount = findViewById(R.id.tvFoodAmount);
        spinnerPond = findViewById(R.id.spinnerPond);
        llPondSelection = findViewById(R.id.llPondSelection);
        tvSelectedPond = findViewById(R.id.tvSelectedPond);
    }

    private void initViewModels() {
        fishViewModel = new ViewModelProvider(this).get(FishViewModel.class);
        pondViewModel = new ViewModelProvider(this).get(PondViewModel.class);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedImageUri = imageUri.toString();
                            ivFishPreview.setImageURI(imageUri);
                        }
                    }
                }
        );
    }

    private void setupListeners() {
        // Tính lượng thức ăn khi nhập cân nặng
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateFoodAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnSaveFish.setOnClickListener(v -> saveFish());
    }

    private void calculateFoodAmount() {
        String weightStr = etWeight.getText().toString();
        if (!weightStr.isEmpty()) {
            try {
                double weight = Double.parseDouble(weightStr);
                double foodAmount = fishViewModel.calculateFoodAmount(weight);
                tvFoodAmount.setText(String.format("Lượng thức ăn: %.2f gram/ngày", foodAmount));
            } catch (NumberFormatException e) {
                tvFoodAmount.setText("Lượng thức ăn: 0 gram/ngày");
            }
        } else {
            tvFoodAmount.setText("Lượng thức ăn: 0 gram/ngày");
        }
    }

    private void loadPonds() {
        SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
        long currentUserId = prefs.getLong("current_user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pondViewModel.getPondsByUserId(currentUserId).observe(this, ponds -> {
            if (ponds != null) {
                pondList = ponds;
                // Nếu không có preSelectedPondId, setup spinner
                if (preSelectedPondId == -1) {
                    setupSpinner(ponds);
                }
            }
        });
    }
    
    private void setupPondSelection() {
        if (preSelectedPondId != -1) {
            // Có POND_ID: ẩn spinner, hiển thị TextView với thông tin hồ
            llPondSelection.setVisibility(android.view.View.GONE);
            tvSelectedPond.setVisibility(android.view.View.VISIBLE);
            // Load thông tin hồ để hiển thị
            pondViewModel.getPondById(preSelectedPondId).observe(this, pond -> {
                if (pond != null) {
                    tvSelectedPond.setText("Hồ đã chọn: Hồ số " + pond.pondId + " (Thể tích: " + (int)pond.volumeLiters + " L)");
                }
            });
        } else {
            // Không có POND_ID: hiển thị spinner, ẩn TextView
            llPondSelection.setVisibility(android.view.View.VISIBLE);
            tvSelectedPond.setVisibility(android.view.View.GONE);
        }
    }
    
    private void setupSpinner(List<Pond> ponds) {
        if (ponds == null || ponds.isEmpty()) {
            return;
        }
        
        // Tạo danh sách tên hồ để hiển thị
        List<String> pondNames = new ArrayList<>();
        for (Pond pond : ponds) {
            pondNames.add("Hồ số " + pond.pondId + " (Thể tích: " + (int)pond.volumeLiters + " L)");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, pondNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPond.setAdapter(adapter);
        
        spinnerPond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position >= 0 && position < ponds.size()) {
                    preSelectedPondId = ponds.get(position).pondId;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                preSelectedPondId = -1;
            }
        });
    }

    private void loadFishData(long fishId) {
        fishViewModel.getFishById(fishId).observe(this, fish -> {
            if (fish != null) {
                etFishName.setText(fish.fishName);
                etFishColor.setText(fish.fishColor);
                etLength.setText(String.valueOf(fish.length));
                etWeight.setText(String.valueOf(fish.weight));
                selectedImageUri = fish.fishImg != null ? fish.fishImg : "";
                if (!selectedImageUri.isEmpty()) {
                    ivFishPreview.setImageURI(Uri.parse(selectedImageUri));
                }
                
                // Lưu pondId từ fish đang edit
                preSelectedPondId = fish.pondId;
                // Cập nhật lại UI sau khi có preSelectedPondId
                setupPondSelection();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private static final double MIN_LENGTH_CM = 0.5;
    private static final double MAX_LENGTH_CM = 140.0;
    private static final double MIN_WEIGHT_G = 1.0;
    private static final double MAX_WEIGHT_G = 40000.0;
    private void saveFish() {
        String name = etFishName.getText().toString().trim();
        String color = etFishColor.getText().toString().trim();
        String lengthStr = etLength.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        if (name.isEmpty() || color.isEmpty() || lengthStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        //ràng buộc tạo hồ trước
        if (pondList.isEmpty()) {
            Toast.makeText(this, "Vui lòng tạo hồ trước", Toast.LENGTH_SHORT).show();
            return;
        }

        double length;
        double weight;
        try {
            length = Double.parseDouble(lengthStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Độ dài không hợp lệ", Toast.LENGTH_SHORT).show();
            etLength.requestFocus();
            return;
        }
        try {
            weight = Double.parseDouble(weightStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cân nặng không hợp lệ", Toast.LENGTH_SHORT).show();
            etWeight.requestFocus();
            return;
        }

        // validate ranges BEFORE calling ViewModel
        if (length < MIN_LENGTH_CM || length > MAX_LENGTH_CM) {
            Toast.makeText(this, "Độ dài phải nằm trong khoảng " + MIN_LENGTH_CM + " - " + (int)MAX_LENGTH_CM + " cm", Toast.LENGTH_LONG).show();
            etLength.requestFocus();
            return;
        }
        if (weight < MIN_WEIGHT_G || weight > MAX_WEIGHT_G) {
            Toast.makeText(this, "Cân nặng phải nằm trong khoảng " + (int)MIN_WEIGHT_G + " - " + (int)MAX_WEIGHT_G + " g", Toast.LENGTH_LONG).show();
            etWeight.requestFocus();
            return;
        }

        // Kiểm tra xem có pondId không
        long pondId = preSelectedPondId;
        if (pondId == -1) {
            // Nếu không có POND_ID, yêu cầu chọn từ spinner
            Toast.makeText(this, "Vui lòng chọn hồ trước khi thêm cá", Toast.LENGTH_SHORT).show();
            return;
        }
        double foodAmount = fishViewModel.calculateFoodAmount(weight);

        if (editFishId == -1) {
            Fish fish = new Fish(pondId, name, color, length, weight,
                    System.currentTimeMillis(), foodAmount, selectedImageUri);
            fishViewModel.insert(fish);
            Toast.makeText(this, "Đã thêm cá thành công", Toast.LENGTH_SHORT).show();
        } else {
            Fish fish = new Fish(pondId, name, color, length, weight,
                    System.currentTimeMillis(), foodAmount, selectedImageUri);
            fish.fishId = editFishId;
            fishViewModel.update(fish);
            Toast.makeText(this, "Đã cập nhật cá thành công", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }
}