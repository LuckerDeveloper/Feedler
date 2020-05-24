package com.example.feedler;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Post.class}, version = 2, exportSchema = false)
public abstract class PostRoomDatabase extends RoomDatabase {

    public abstract PostDao postDao();

    private static PostRoomDatabase INSTANCE;

    static PostRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
//            synchronized (PostRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PostRoomDatabase.class, "post_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
//            }
        }
        return INSTANCE;
    }
}

