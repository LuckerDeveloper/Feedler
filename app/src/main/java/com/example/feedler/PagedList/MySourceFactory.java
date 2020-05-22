package com.example.feedler.PagedList;

import android.content.Context;

import androidx.paging.DataSource;

import com.example.feedler.Post;
import com.example.feedler.PostRepository;

public class MySourceFactory extends DataSource.Factory<Integer, Post> {

    private final PostRepository postStorage;
    private Context context;

    public MySourceFactory(PostRepository postStorage, Context context) {
        this.postStorage = postStorage;
        this.context=context;
    }

    @Override
    public DataSource create() {
        return new MyPositionalDataSource(postStorage, context);
    }
}
