package com.example.feedler;

import android.content.Intent;

import androidx.room.Room;

import com.example.feedler.Favorites.FavoriteDatabase;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

public class Application extends android.app.Application {

    private PostRoomDatabase postRoomDatabase;
    private FavoriteDatabase favoriteDatabase;
    public static Application instance;

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Intent intent = new Intent(Application.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);

        instance = this;
        postRoomDatabase= Room.databaseBuilder(this, PostRoomDatabase.class, "post_database")
                .allowMainThreadQueries()
                .build();

        favoriteDatabase= Room.databaseBuilder(this, FavoriteDatabase.class, "favorite_database")
                .allowMainThreadQueries()
                .build();
    }

    public static Application getInstance(){
        return instance;
    }

    public PostRoomDatabase getPostRoomDatabase() {
        return postRoomDatabase;
    }

    public  FavoriteDatabase getFavoriteDatabase(){ return favoriteDatabase;}
}
