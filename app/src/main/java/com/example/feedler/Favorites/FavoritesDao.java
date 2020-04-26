package com.example.feedler.Favorites;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.feedler.Post;

import java.util.List;


@Dao
public interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Post post);

    @Query("SELECT * FROM POST_TABLE")
    List<Post> getAll();

    @Delete
    void delete (Post post);

    @Query("DELETE FROM post_table")
    void deleteAll();
}


