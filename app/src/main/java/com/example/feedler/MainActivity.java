package com.example.feedler;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.PagedList.PostAdapter;
import com.vk.sdk.VKSdk;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PostAdapter adapter = new PostAdapter(new PostRepository.PostDiffUtilCallback());
    PostRepository.PostDiffUtilCallback diffUtilCallback;

    private PostViewModel model;



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
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);
            }
                return true;
            case R.id.action_refresh:
            {
                //метод для обновления данных
            }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (!VKSdk.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
        }
        recyclerView = findViewById(R.id.recyclerView);

        model= ViewModelProviders.of(this).get(PostViewModel.class);

        adapter = new PostAdapter(diffUtilCallback);

        model.getAllPosts().observe(this, new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(PagedList<Post> posts) {
                adapter.submitList(posts);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
    }
}




