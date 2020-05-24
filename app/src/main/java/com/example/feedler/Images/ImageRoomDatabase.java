package com.example.feedler.Images;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.feedler.Post;
import com.example.feedler.PostDao;

@Database(entities = {Image.class}, version = 1, exportSchema = false)
public abstract class ImageRoomDatabase extends RoomDatabase {

    public abstract ImageDao imageDao();
    private static com.example.feedler.Images.ImageRoomDatabase INSTANCE;

    public static com.example.feedler.Images.ImageRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (com.example.feedler.Images.ImageRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            com.example.feedler.Images.ImageRoomDatabase.class, "image_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


