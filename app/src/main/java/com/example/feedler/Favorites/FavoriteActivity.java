package com.example.feedler.Favorites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.AppExecutors;
import com.example.feedler.MainActivity;
import com.example.feedler.PagedList.PostAdapter;
import com.example.feedler.Post;
import com.example.feedler.PostRepository;
import com.example.feedler.PostViewModel;
import com.example.feedler.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements PostAdapter.Listener , PostRepository.FavoriteCallBack {

    RecyclerView recyclerView;
    FavoriteAdapter adapter;
    PostViewModel model;
    List<Post> posts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_list);

        model= new ViewModelProvider(this).get(PostViewModel.class);
        model.getFavoritePost(this);

        recyclerView = findViewById(R.id.recyclerViewFavorite);
        adapter = new FavoriteAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setPosts(posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_favorite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
            {
                model.deleteFavoriteAll();
                Intent intent=new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.action_settings:
            {
                //меню настроек
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void insertFavorite(Post post) {
        model.insertFavorite(post);
    }

    @Override
    public void deleteFavorite(Post post) {
        model.deleteFavorite(post);
    }

    @Override
    public void onSuccess(Object result) {
        posts= (List<Post>) result;
        adapter.setPosts(posts);

    }

    @Override
    public void onFail() {
        AppExecutors.getInstance().mainThread().execute(() -> Toast.makeText(getApplicationContext(),
                "Сохраненные посты отсутствуют",
                Toast.LENGTH_SHORT).show());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
