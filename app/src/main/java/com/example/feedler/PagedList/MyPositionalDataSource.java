package com.example.feedler.PagedList;

import android.content.Context;
import android.net.sip.SipSession;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.PositionalDataSource;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.feedler.Application;
import com.example.feedler.Post;
import com.example.feedler.PostDao;
import com.example.feedler.PostRoomDatabase;


import java.util.ArrayList;
import java.util.List;

public class MyPositionalDataSource extends PositionalDataSource<Post>{


    private final Context context;



    public MyPositionalDataSource(Context context) {

        this.context = context;

    }

    @Override
    public void loadInitial(final @NonNull LoadInitialParams params, final @NonNull LoadInitialCallback<Post> callback) {
        List<Post> result = new ArrayList<>();
        PostRoomDatabase db = PostRoomDatabase.getDatabase(context);
        PostDao postDao = db.postDao();
        result = postDao.findPostsBetween(params.requestedStartPosition,(params.requestedStartPosition+ params.requestedLoadSize));
        callback.onResult(result, 0);

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, final @NonNull LoadRangeCallback<Post> callback) {
        List<Post> result = new ArrayList<>();
        PostRoomDatabase db = PostRoomDatabase.getDatabase(context);
        PostDao postDao = db.postDao();
        result = postDao.findPostsBetween(params.startPosition, params.startPosition+params.loadSize);

        callback.onResult(result);

    }

}
