package com.example.feedler.PagedList;

import androidx.paging.DataSource;

import com.example.feedler.Post;
import com.example.feedler.PostRepository;



public class MySourceFactory extends DataSource.Factory<Integer, Post> {

    private final PostRepository postStorage;

    public MySourceFactory(PostRepository postStorage) {
        this.postStorage = postStorage;
    }

    @Override
    public DataSource create() {
        return new MyPositionalDataSource(postStorage);
    }
}
