package com.example.feedler;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.feedler.PagedList.MySourceFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class PostViewModel extends AndroidViewModel   {

    private PostRepository postRepository;
    private LiveData<PagedList<Post>> allPosts;
    public static Post postForTransmission;

    public PostViewModel (Application application ) {
        super(application);
        postRepository = new PostRepository(application);
    }

    LiveData<PagedList<Post>> getAllPosts(Context context) {
        MySourceFactory sourceFactory = new MySourceFactory(postRepository, context);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .build();

        allPosts = new LivePagedListBuilder<>(sourceFactory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();
        return allPosts;
    }

    public void insertFavorite(Post post) {
        postRepository.insertFavorite(post);
    }

    public void getFavoritePost(Context context){  postRepository.getFavoritePost(context) ;}

    public void getSearchFavoritePost(Context context, String search){  postRepository.getSearchFavoritePost(context, search) ;}

    public void getSavedPostFromDB(Context  context){postRepository.getSavedPostFromDatabase(context);    }

    public void deleteFavorite(Post post) {postRepository.deleteFavorite(post);}

    public void deleteFavoriteAll(){postRepository.deleteFavoriteAll();}

}
