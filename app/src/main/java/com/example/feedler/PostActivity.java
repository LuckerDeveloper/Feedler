package com.example.feedler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class PostActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_window_fav);
        //Activity для просмотра поста отдельно
    }
}
