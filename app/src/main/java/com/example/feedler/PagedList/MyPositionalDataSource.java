package com.example.feedler.PagedList;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.example.feedler.Post;

import java.util.List;

public class MyPositionalDataSource extends PositionalDataSource<Post> {

    private final PostStorage postStorage;

    public MyPositionalDataSource(PostStorage postStorage) {
        this.postStorage = postStorage;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Post> callback) {
        List<Post> result = postStorage.getData(params.requestedStartPosition, params.requestedLoadSize);
        callback.onResult(result, 0);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Post> callback) {
        List<Post> result = postStorage.getData(params.startPosition, params.loadSize);
        callback.onResult(result);
    }

}