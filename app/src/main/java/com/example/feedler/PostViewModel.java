package com.example.feedler;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.feedler.PagedList.MySourceFactory;


import java.util.concurrent.Executors;

public class PostViewModel extends AndroidViewModel {

    private PostRepository postRepository;
    private LiveData<PagedList<Post>> allPosts;

    public PostViewModel (Application application) {
        super(application);
        postRepository = new PostRepository();
    }

    public LiveData<PagedList<Post>> getAllPosts() {
        MySourceFactory sourceFactory = new MySourceFactory(new PostRepository());

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .build();

        allPosts = new LivePagedListBuilder<>(sourceFactory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();
        return allPosts;
    }

}
