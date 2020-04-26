package com.example.feedler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.feedler.Favorites.FavoriteActivity;
import com.example.feedler.PagedList.PostAdapter;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PostAdapter.Listener{
    RecyclerView recyclerView;
    PostAdapter adapter;
    PostRepository.PostDiffUtilCallback diffUtilCallback;
    PostViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.srl_container);

        if (!VKSdk.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
        } else {
            recyclerView = findViewById(R.id.recyclerView);

            adapter = new PostAdapter(diffUtilCallback, this);
            model= new ViewModelProvider(this).get(PostViewModel.class);

            model.getAllPosts().observe(this, new Observer<PagedList<Post>>() {
                @Override
                public void onChanged(PagedList<Post> posts) {
                    adapter.submitList(posts);
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            recyclerView.setAdapter(adapter);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    model.getAllPosts().observe(MainActivity.this, new Observer<PagedList<Post>>() {
                        @Override
                        public void onChanged(PagedList<Post> posts) {
                            adapter.submitList(posts);
                        }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                    recyclerView.setAdapter(adapter);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
            {
                if(model.getFavoritePost().size()==0){
                    Toast.makeText(getApplicationContext(),
                            "Сохраненные посты отсутствуют",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                }
            }
            return true;
            case R.id.action_settings:
            {
                //меню настроек
            }
            return true;
            case R.id.authorization:
            {
                //меню авторизации
            }
            case R.id.exit:
            {
                //меню выхода
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void insertFavorite(Post post) {
        List<Post> postList=model.getFavoritePost();
        if (postList.size()!=0) {
            post.id=postList.get(postList.size()-1).id+1;
        } else {
            post.id=0;
        }
        for (int i =0 ; i<postList.size(); i++){
            if ( post.isEquals(postList.get(i)) ){
                post.id=postList.get(i).id;
                break;
            }
        }
        model.insertFavorite(post);
    }

    @Override
    public void deleteFavorite(Post post) {
        List<Post> favoritePost = model.getFavoritePost();
        if(favoritePost!=null){
            for (int i =0 ; i<favoritePost.size(); i++){
                if ( post.isEquals(favoritePost.get(i)) ){
                    model.deleteFavorite(favoritePost.get(i));
                    break;
                }
            }
        }

    }

    @Override
    public void replaceFavoriteVar(Post post) {
        model.replaceFavoriteVar(post);
    }
}
