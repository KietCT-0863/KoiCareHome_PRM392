package com.example.koicarehome_prm392.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.koicarehome_prm392.data.dao.FishDao;
import com.example.koicarehome_prm392.data.db.AppDatabase;
import com.example.koicarehome_prm392.data.entities.Fish;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FishRepository {
    private FishDao fishDao;
    private ExecutorService executorService;

    public FishRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        fishDao = db.fishDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Tính lượng thức ăn theo công thức: weight(gram) x 0.01
    public double calculateFoodAmount(double weightGrams) {
        return weightGrams * 0.01;
    }

    public void insert(Fish fish) {
        executorService.execute(() -> {
            // Tính lượng thức ăn trước khi insert
            fish.foodAmount = calculateFoodAmount(fish.weight);
            fishDao.insert(fish);
        });
    }

    public void update(Fish fish) {
        executorService.execute(() -> {
            // Tính lại lượng thức ăn khi update
            fish.foodAmount = calculateFoodAmount(fish.weight);
            fishDao.update(fish);
        });
    }

    public void delete(Fish fish) {
        executorService.execute(() -> fishDao.delete(fish));
    }

    public LiveData<List<Fish>> getFishByPondId(long pondId) {
        return fishDao.getFishByPondId(pondId);
    }

    public LiveData<List<Fish>> getAllFish() {
        return fishDao.getAllFish();
    }

    public LiveData<Fish> getFishById(long fishId) {
        return fishDao.getFishById(fishId);
    }

    public LiveData<List<Fish>> getFishByUserId(long userId) {
        return fishDao.getFishByUserId(userId);
    }
}
