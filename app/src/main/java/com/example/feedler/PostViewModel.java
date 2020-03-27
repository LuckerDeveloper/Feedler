package com.example.feedler;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class PostViewModel extends AndroidViewModel {

    private PostRepository postRepository;
    private LiveData<List<Post>> allPosts;

    public PostViewModel (Application application) {
        super(application);
        postRepository = new PostRepository(application);
        allPosts = postRepository.getAllPosts();
    }

    LiveData<List<Post>> getAllPosts() { return allPosts; }

}
