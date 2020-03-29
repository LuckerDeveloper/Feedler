package com.example.feedler;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PostRepository {

    private PostDao postDao;
    private LiveData<List<Post>> allPosts;

    PostRepository(Application application) {
        //
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Post>> getAllPosts() {
        return allPosts;
    }

}
