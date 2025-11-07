package com.example.koicarehome_prm392.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.koicarehome_prm392.data.entities.Fish;
import java.util.List;

@Dao
public interface FishDao {
    @Insert
    long insert(Fish fish);

    @Update
    void update(Fish fish);

    @Delete
    void delete(Fish fish);

    @Query("SELECT * FROM fish WHERE fishId = :fishId")
    LiveData<Fish> getFishById(long fishId);
    @Query("SELECT * FROM fish WHERE pondId = :pondId ORDER BY addDate DESC")
    LiveData<List<Fish>> getFishByPondId(long pondId);
    @Query("SELECT * FROM fish ORDER BY addDate DESC")
    LiveData<List<Fish>> getAllFish();
    @Query("DELETE FROM fish WHERE fishId = :fishId")
    void deleteById(long fishId);
    
    @Query("SELECT COUNT(*) FROM fish WHERE pondId = :pondId")
    int getFishCountByPondId(long pondId);
    
    @Query("SELECT SUM(foodAmount) FROM fish WHERE pondId = :pondId")
    double getTotalFoodAmountByPondId(long pondId);
    
    @Query("SELECT COALESCE(SUM(f.foodAmount), 0) FROM fish f " +
           "INNER JOIN ponds p ON f.pondId = p.pondId WHERE p.userId = :userId")
    double getTotalFoodAmountByUserId(long userId);
    
    @Query("SELECT f.* FROM fish f " +
           "INNER JOIN ponds p ON f.pondId = p.pondId WHERE p.userId = :userId ORDER BY f.addDate DESC")
    LiveData<List<Fish>> getFishByUserId(long userId);
//    @Query("SELECT * FROM fish WHERE pondId = :pondId")
//    List<Fish> getFishForPond(long pondId);
    //đã có hàm get fishbypond ở trên

}
