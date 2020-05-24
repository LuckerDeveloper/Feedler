package com.example.feedler.Images;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.feedler.Post;

import java.util.List;

@Dao
public interface ImageDao {

    @Query("SELECT * FROM image_table WHERE postId =:postId")
    List<Image> getByPostId(long postId);

    @Insert
    void insertImages(List<Image> imageList);

    @Query("DELETE FROM image_table WHERE postId=:postId")
    void deleteImagesByPostId(long postId);

    @Query("SELECT * FROM image_table")
    List<Image> getAll();

    @Query("DELETE FROM image_table")
    void deleteAll();
}
