package com.example.feedler;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM POST_TABLE")
    List<Post> getAll();

    @Query("SELECT * FROM POST_TABLE ORDER BY data DESC LIMIT 30 ")
    List<Post> getSaved();

    @Query("SELECT * FROM POST_TABLE WHERE favorite = 1 ORDER BY data DESC ")
    List<Post> getFavorite();

    @Query("SELECT * FROM POST_TABLE WHERE favorite = 1 AND text LIKE :search OR group_name LIKE :search AND favorite = 1 ORDER BY data DESC")
    List<Post> getFavoriteSearching(String search);

    @Query("SELECT * FROM POST_TABLE WHERE group_name =:groupName AND data =:date AND text=:textPost ")
    Post getByParams(String groupName, long date , String textPost);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(Post post);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Post> list);

    @Query("DELETE FROM post_table WHERE favorite=0")
    void deleteSaved();

    @Query("DELETE FROM post_table WHERE favorite=1")
    void deleteFavorites();

    @Delete
    int deletePost(Post post);

}
