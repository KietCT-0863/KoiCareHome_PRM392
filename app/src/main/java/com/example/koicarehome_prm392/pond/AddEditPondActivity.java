// File: com/example/koicarehome_prm392/pond/AddEditPondActivity.java
package com.example.koicarehome_prm392.pond;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.koicarehome_prm392.R;
import com.example.koicarehome_prm392.data.entities.Pond;
import com.example.koicarehome_prm392.viewmodel.PondViewModel;

public class AddEditPondActivity extends AppCompatActivity {

    // *** THÊM MỚI: Các hằng số để truyền dữ liệu qua Intent ***
    public static final String EXTRA_POND_ID = "com.example.koicarehome_prm392.EXTRA_POND_ID";
    public static final String EXTRA_POND_VOLUME = "com.example.koicarehome_prm392.EXTRA_POND_VOLUME";

    private EditText etPondVolume;
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
        btnSavePond = findViewById(R.id.btnSavePond);
        btnCancel = findViewById(R.id.btnCancel);
        pondViewModel = new ViewModelProvider(this).get(PondViewModel.class);

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
        } else {
            setTitle("Thêm hồ mới");
        }

        btnSavePond.setOnClickListener(v -> savePond());
        btnCancel.setOnClickListener(v -> finish());
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
}