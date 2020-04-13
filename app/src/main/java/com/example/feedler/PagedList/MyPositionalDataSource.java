package com.example.feedler.PagedList;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.example.feedler.Post;
import com.example.feedler.PostRepository;

import java.util.List;

public class MyPositionalDataSource extends PositionalDataSource<Post> {

    private final PostRepository postStorage;

    protected String mNextFrom = null;

    public MyPositionalDataSource(PostRepository postStorage) {
        this.postStorage = postStorage;
    }

    @Override
    public void loadInitial(final @NonNull LoadInitialParams params, final @NonNull LoadInitialCallback<Post> callback) {
        Log.d("MyPositionalDataSource", "loadInitial");
        final PostRepository.Callback<List<Post>> postCallback = new PostRepository.Callback<List<Post>>() {
            @Override
            public void onResult(String nextFrom, List<Post> result, Throwable error) {
                mNextFrom = nextFrom;
                callback.onResult(result, params.requestedStartPosition);
            }
        };

        Log.d("MyPositionalDataSource", String.format("load(%s, %s)", mNextFrom, params.requestedLoadSize));
        postStorage.getData(mNextFrom, params.requestedLoadSize, postCallback);

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, final @NonNull LoadRangeCallback<Post> callback) {
        Log.d("MyPositionalDataSource", "loadRange");
        final PostRepository.Callback<List<Post>> postCallback = new PostRepository.Callback<List<Post>>() {
            @Override
            public void onResult(String nextFrom, List<Post> result, Throwable error) {
                mNextFrom = nextFrom;
                callback.onResult(result);
            }
        };


        Log.d("MyPositionalDataSource", String.format("load(%s, %s)", mNextFrom, params.loadSize));
        postStorage.getData(mNextFrom, params.loadSize, postCallback);
    }
}