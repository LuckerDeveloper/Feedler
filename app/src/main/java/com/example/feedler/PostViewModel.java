package com.example.feedler;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class PostViewModel extends AndroidViewModel {

    private PostRepository postRepository;
    private LiveData<ArrayList<String>> allPosts;

    public PostViewModel (Application application) {
        super(application);
        postRepository = new PostRepository(application);
    }

    public LiveData<ArrayList<String>> getAllPosts() {
        allPosts = postRepository.getAllPosts();
        return allPosts;
    }

}
