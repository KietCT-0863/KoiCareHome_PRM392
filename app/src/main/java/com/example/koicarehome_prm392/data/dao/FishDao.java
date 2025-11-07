package com.example.koicarehome_prm392.data.dao;

<<<<<<< HEAD
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
=======
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
>>>>>>> origin/main

import com.example.koicarehome_prm392.data.entities.Fish;
import java.util.List;

@Dao
public interface FishDao {
    @Insert
    long insert(Fish fish);

<<<<<<< HEAD
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
=======
    @Query("SELECT * FROM fish WHERE pondId = :pondId")
    List<Fish> getFishForPond(long pondId);
>>>>>>> origin/main
}
