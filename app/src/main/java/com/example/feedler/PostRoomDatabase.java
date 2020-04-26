package com.example.feedler;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Post.class}, version = 1, exportSchema = false)
public abstract class PostRoomDatabase extends RoomDatabase {

    public abstract PostDao postDao();
}
