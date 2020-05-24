package com.example.feedler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.Favorites.FavoriteAdapter;
import com.example.feedler.Images.Image;
import com.example.feedler.InnerWebBrowser.WebActivity;
import com.example.feedler.PagedList.PostAdapter;
import com.example.feedler.PagedList.PostViewHolder;
import com.squareup.picasso.Picasso;

import org.bluecabin.textoo.LinksHandler;
import org.bluecabin.textoo.Textoo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PostActivity extends AppCompatActivity implements PostAdapter.Listener{

    private PostViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_list);

        Post post = PostViewModel.postForTransmission;
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFavorite);
        FavoriteAdapter adapter = new FavoriteAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setPosts(postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        model=new ViewModelProvider(this).get(PostViewModel.class);

    }

    @Override
    public void insertFavorite(Post post) {
        model.insertFavorite(post);
    }

    @Override
    public void deleteFavorite(Post post) {
        model.deleteFavorite(post);
    }
}