package com.example.feedler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.feedler.Favorites.FavoriteActivity;
import com.example.feedler.Favorites.FavoriteAdapter;
import com.example.feedler.PagedList.PostAdapter;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PostAdapter.Listener, PostRepository.CallbackWithListPost{

    public static final String APP_PREFERENCES = "Settings";
    public static final String APP_PREFERENCES_INNER_BROWSER = "APP_PREFERENCES_INNER_BROWSER";
    RecyclerView recyclerView;
    PostAdapter adapter;
    PostRepository.PostDiffUtilCallback diffUtilCallback;
    public PostViewModel model;
    private SharedPreferences mSettings;
    boolean isInInnerBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        final SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.srl_container);

        if (!VKSdk.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
        } else {
            recyclerView = findViewById(R.id.recyclerView);

            adapter = new PostAdapter(diffUtilCallback, this);
            model= new ViewModelProvider(this).get(PostViewModel.class);

            model.getAllPosts(this).observe(this, new Observer<PagedList<Post>>() {
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

                    mSwipeRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.getAllPosts(MainActivity.this).observe(MainActivity.this, new Observer<PagedList<Post>>() {
                                @Override
                                public void onChanged(PagedList<Post> posts) {
                                    adapter.submitList(posts);
                                }
                            });
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            recyclerView.setAdapter(adapter);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    } , 500);
                }
            });
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        if (mSettings.contains(APP_PREFERENCES_INNER_BROWSER)){
            isInInnerBrowser=mSettings.getBoolean(APP_PREFERENCES_INNER_BROWSER, false);
            MenuItem menuItemInnerBrowser= menu.findItem(R.id.action_settings);
            menuItemInnerBrowser.setChecked(isInInnerBrowser);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite: {
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.action_settings: {
                item.setChecked(!item.isChecked());
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putBoolean(APP_PREFERENCES_INNER_BROWSER, item.isChecked());
                editor.apply();
                //меню настроек
            }
            return true;
            case R.id.authorization: {
                //меню авторизации
                return true;
            }
            case R.id.exit: {
                VKSdk.logout();
                Intent intent = new Intent(MainActivity.this, AuthorizationActivity.class);
                startActivity(intent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
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
        FavoriteAdapter favoriteAdapter = new FavoriteAdapter(this );
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                List<Post> postList= (List<Post>) result;
                recyclerView.setAdapter(favoriteAdapter);
                favoriteAdapter.setPosts(postList);
            }
        });

    }

    @Override
    public void onFail() {
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "Сохраненные посты отсутствуют",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
