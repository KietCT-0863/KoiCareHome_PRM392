package com.example.koicarehome_prm392.pond;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog; // *** THÊM IMPORT NÀY ***
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koicarehome_prm392.R;
import com.example.koicarehome_prm392.adapter.PondAdapter;
import com.example.koicarehome_prm392.data.entities.Pond;
import com.example.koicarehome_prm392.viewmodel.PondViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PondListActivity extends AppCompatActivity {

    private PondViewModel pondViewModel;
    private long currentUserId;
    private PondAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pond_list);

        Toolbar toolbar = findViewById(R.id.toolbar_pond_list);
        setSupportActionBar(toolbar);
        setTitle("Danh sách hồ cá");

        SharedPreferences prefs = getSharedPreferences("users", MODE_PRIVATE);
        currentUserId = prefs.getLong("current_user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPonds);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new PondAdapter();
        recyclerView.setAdapter(adapter);

        pondViewModel = new ViewModelProvider(this).get(PondViewModel.class);

        pondViewModel.getPondsByUserId(currentUserId).observe(this, adapter::setPonds);

        FloatingActionButton fabAddPond = findViewById(R.id.fabAddPond);
        fabAddPond.setOnClickListener(v -> {
            Intent intent = new Intent(PondListActivity.this, AddEditPondActivity.class);
            startActivity(intent);
        });

        // *** XÓA BỎ HOÀN TOÀN KHỐI new ItemTouchHelper(...) VÀ adapter.setOnItemClickListener(...) CŨ ***

        // *** THÊM MỚI: Xử lý sự kiện click cho các icon ***
        adapter.setOnItemActionClickListener(new PondAdapter.OnItemActionClickListener() {
            @Override
            public void onEditClick(Pond pond) {
                // Mở màn hình sửa với thông tin của hồ được chọn
                Intent intent = new Intent(PondListActivity.this, AddEditPondActivity.class);
                intent.putExtra(AddEditPondActivity.EXTRA_POND_ID, pond.pondId);
                intent.putExtra(AddEditPondActivity.EXTRA_POND_VOLUME, pond.volumeLiters);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Pond pond) {
                // Hiển thị hộp thoại xác nhận trước khi xóa
                new AlertDialog.Builder(PondListActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa " + "Hồ số: " + pond.pondId + "?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            // Nếu người dùng chọn "Xóa", thì mới thực hiện xóa
                            pondViewModel.delete(pond);
                            Toast.makeText(PondListActivity.this, "Đã xóa hồ", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null) // Không làm gì nếu người dùng chọn "Hủy"
                        .show();
            }
        });
    }
}