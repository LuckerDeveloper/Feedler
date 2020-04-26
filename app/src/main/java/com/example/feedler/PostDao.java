package com.example.feedler;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM POST_TABLE ORDER BY id")
    List<Post> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Post> list);

    @Query("DELETE FROM post_table")
    void deleteALL();

    @Query("SELECT * FROM POST_TABLE WHERE id> :mId")
    List<Post> getAfterId(int mId);
}
