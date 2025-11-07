package com.example.koicarehome_prm392.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.koicarehome_prm392.Repositories.FishRepository;
import com.example.koicarehome_prm392.data.entities.Fish;

import java.util.List;

public class FishViewModel extends AndroidViewModel {
    private FishRepository repository;
    private LiveData<List<Fish>> allFish;

    public FishViewModel(@NonNull Application application) {
        super(application);
        repository = new FishRepository(application);
        allFish = repository.getAllFish();
    }

    public void insert(Fish fish) {
        repository.insert(fish);
    }

    public void update(Fish fish) {
        repository.update(fish);
    }

    public void delete(Fish fish) {
        repository.delete(fish);
    }

    public LiveData<List<Fish>> getAllFish() {
        return allFish;
    }

    public LiveData<List<Fish>> getFishByPondId(long pondId) {
        return repository.getFishByPondId(pondId);
    }

    public LiveData<Fish> getFishById(long fishId) {
        return repository.getFishById(fishId);
    }

    public LiveData<List<Fish>> getFishByUserId(long userId) {
        return repository.getFishByUserId(userId);
    }

    // Helper method để tính lượng thức ăn
    public double calculateFoodAmount(double weightGrams) {
        return repository.calculateFoodAmount(weightGrams);
    }
}