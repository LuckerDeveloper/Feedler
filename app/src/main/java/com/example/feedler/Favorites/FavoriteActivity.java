package com.example.feedler.Favorites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

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
import java.util.Timer;
import java.util.TimerTask;

public class FavoriteActivity extends AppCompatActivity implements PostAdapter.Listener , PostRepository.CallbackWithListPost {

    RecyclerView recyclerView;
    FavoriteAdapter adapter;
    PostViewModel model;
    List<Post> posts;
    Button mainBut;
    EditText edit;
    Toolbar toolbarF;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_list);
        mainBut = findViewById(R.id.mainFeedFAV);
        mainBut.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
        });
        edit = findViewById(R.id.search);

        toolbarF = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbarF);
        model= new ViewModelProvider(this).get(PostViewModel.class);
        model.getFavoritePost(this);

        recyclerView = findViewById(R.id.recyclerViewFavorite);
        adapter = new FavoriteAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setPosts(posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        edit.addTextChangedListener(new TextWatcher() {

            CountDownTimer timer;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new CountDownTimer(500,500) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        model = new ViewModelProvider(FavoriteActivity.this).get(PostViewModel.class);
                        model.getSearchFavoritePost(FavoriteActivity.this, edit.getText().toString());

                        recyclerView = findViewById(R.id.recyclerViewFavorite);
                        adapter = new FavoriteAdapter(FavoriteActivity.this);
                        recyclerView.setAdapter(adapter);
                        adapter.setPosts(posts);
                        recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this));

                    }
                }.start();

            }



            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
