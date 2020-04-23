package com.example.feedler;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.feedler.PagedList.MySourceFactory;


import java.util.List;
import java.util.concurrent.Executors;

public class PostViewModel extends AndroidViewModel {

    private PostRepository postRepository;
    private LiveData<PagedList<Post>> allPosts;

    public PostViewModel (Application application) {
        super(application);
        postRepository = new PostRepository(application);
    }

    LiveData<PagedList<Post>> getAllPosts() {
        MySourceFactory sourceFactory = new MySourceFactory(getApplication());

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .build();


        LiveData<PagedList<Post>> pagedListLiveData = new LivePagedListBuilder<>(sourceFactory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();
        return pagedListLiveData;
    }

    public void insert(Post post) {
        postRepository.insert(post);
    }

}
