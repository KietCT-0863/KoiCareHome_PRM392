// File: com/example/koicarehome_prm392/viewmodel/PondViewModel.java
package com.example.koicarehome_prm392.viewmodel;import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.koicarehome_prm392.data.db.AppDatabase;
import com.example.koicarehome_prm392.data.dao.PondDao;
import com.example.koicarehome_prm392.data.entities.Pond;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PondViewModel extends AndroidViewModel {

    private final PondDao pondDao;
    private final ExecutorService executorService;

    public PondViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        pondDao = database.pondDao();
        // Tối ưu: Chỉ tạo một luồng duy nhất để xử lý tất cả các tác vụ CSDL
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Pond pond) {
        executorService.execute(() -> pondDao.insert(pond));
    }

    public void update(Pond pond) {
        executorService.execute(() -> pondDao.update(pond));
    }

    public void delete(Pond pond) {
        executorService.execute(() -> pondDao.delete(pond));
    }

    public LiveData<List<Pond>> getPondsByUserId(long userId) {
        return pondDao.getPondsByUserId(userId);
    }

    // Đảm bảo ExecutorService được đóng lại khi ViewModel bị hủy
    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}