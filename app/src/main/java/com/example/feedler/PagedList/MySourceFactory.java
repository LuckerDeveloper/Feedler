package com.example.feedler.PagedList;

import android.content.Context;

import androidx.paging.DataSource;

import com.example.feedler.Post;
import com.example.feedler.PostDao;
import com.example.feedler.PostRepository;
import com.example.feedler.PostRoomDatabase;

import java.util.List;

public class MySourceFactory extends DataSource.Factory<Integer, Post> {

    private final Context context;

    public MySourceFactory(Context context) {
        this.context = context;
    }

    @Override
    public DataSource create() {

        return new MyPositionalDataSource(context);
    }
}
