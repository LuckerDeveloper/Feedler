package com.example.feedler;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM post_table")
    DataSource.Factory<Integer, Post> getAll();


    @Query("SELECT * FROM post_table WHERE id = :id")
    Post getById(int id);

    @Query("SELECT * FROM post_table WHERE id BETWEEN :from AND :to")
    List<Post> findPostsBetween(int from, int to);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Post post);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Post> list);

    @Update
    void update(Post post);

    @Query("DELETE FROM post_table")
    void deleteALL();

}
