package com.example.koicarehome_prm392.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.koicarehome_prm392.data.dao.FishDao;
import com.example.koicarehome_prm392.data.dao.PondDao;
import com.example.koicarehome_prm392.data.dao.UserDao;
import com.example.koicarehome_prm392.data.entities.Fish;
import com.example.koicarehome_prm392.data.entities.Pond;
import com.example.koicarehome_prm392.data.entities.User;

@Database(entities = {User.class, Pond.class, Fish.class}, version = 1, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract PondDao pondDao();
    public abstract FishDao fishDao();
    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "koicarehome-db")
                            .fallbackToDestructiveMigration()
<<<<<<< HEAD
=======
                            .allowMainThreadQueries()
>>>>>>> origin/main
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
