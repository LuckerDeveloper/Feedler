package com.example.feedler.Favorites;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.feedler.Post;
import com.example.feedler.PostRoomDatabase;

@Database(entities = {Post.class}, version = 1)
public abstract class FavoriteDatabase extends RoomDatabase {

    public abstract FavoritesDao favoritesDao();

    private static FavoriteDatabase INSTANCE;

    public static FavoriteDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FavoriteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FavoriteDatabase.class, "favorite_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
