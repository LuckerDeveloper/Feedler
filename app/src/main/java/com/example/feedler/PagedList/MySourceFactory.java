package com.example.feedler.PagedList;

import androidx.paging.DataSource;

import com.example.feedler.Post;

public class MySourceFactory extends DataSource.Factory<Integer, Post> {

    private final PostStorage postStorage;

    public MySourceFactory(PostStorage postStorage) {
        this.postStorage = postStorage;
    }

    @Override
    public DataSource create() {
        return new MyPositionalDataSource(postStorage);
    }
}
