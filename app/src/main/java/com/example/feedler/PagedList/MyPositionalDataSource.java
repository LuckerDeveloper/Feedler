package com.example.feedler.PagedList;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.example.feedler.Post;
import com.example.feedler.PostRepository;

import java.util.List;

public class MyPositionalDataSource extends PositionalDataSource<Post> {

    private final PostRepository postStorage;
    private Context context;
    private String mNextFrom = null;

    MyPositionalDataSource(PostRepository postStorage, Context context) {
        this.postStorage = postStorage;
        this.context=context;
    }

    @Override
    public void loadInitial(final @NonNull LoadInitialParams params, final @NonNull LoadInitialCallback<Post> callback) {
        final PostRepository.Callback<List<Post>> postCallback = new PostRepository.Callback<List<Post>>() {
            @Override
            public void onResult(String nextFrom, List<Post> result, Throwable error) {
                mNextFrom = nextFrom;
                callback.onResult(result, params.requestedStartPosition);
            }
        };
        postStorage.getData(mNextFrom, params.requestedLoadSize, postCallback,context);

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, final @NonNull LoadRangeCallback<Post> callback) {
        final PostRepository.Callback<List<Post>> postCallback = new PostRepository.Callback<List<Post>>() {
            @Override
            public void onResult(String nextFrom, List<Post> result, Throwable error) {
                mNextFrom = nextFrom;
                callback.onResult(result);
            }
        };
        postStorage.getData(mNextFrom, params.loadSize, postCallback, context);
    }
}