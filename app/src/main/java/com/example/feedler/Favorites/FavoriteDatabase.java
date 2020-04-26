package com.example.feedler.Favorites;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.feedler.Post;

@Database(entities = {Post.class}, version = 1)
public abstract class FavoriteDatabase extends RoomDatabase {
    public abstract FavoritesDao favoritesDao();
}
