package com.example.koicarehome_prm392.data.dao;

<<<<<<< HEAD
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
=======
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
>>>>>>> origin/main

import com.example.koicarehome_prm392.data.entities.Pond;

import java.util.List;

@Dao
public interface PondDao {
    @Insert
    long insert(Pond pond);
<<<<<<< HEAD
    @Query("SELECT * FROM ponds WHERE userId = :userId")
    List<Pond> getPondsForUser(long userId);
=======

    @Update
    void update(Pond pond);

    @Delete
    void delete(Pond pond);

    @Query("SELECT * FROM ponds WHERE userId = :userId")
    List<Pond> getPondsForUser(long userId);

    @Query("SELECT * FROM ponds WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<Pond>> getPondsByUserId(long userId);

    @Query("SELECT * FROM ponds WHERE pondId = :pondId")
    LiveData<Pond> getPondById(long pondId);
>>>>>>> origin/main
}
