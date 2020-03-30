package com.example.feedler;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface PostDao {

    // аннотация с Query
    LiveData<List<Post>> getAlphabetizedPosts();

}
