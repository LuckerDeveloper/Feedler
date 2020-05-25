package com.example.feedler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
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
import com.example.feedler.PagedList.PostViewHolder;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PostViewHolder.Listener, PostRepository.CallbackWithListPost{

    public static final String THEME = "my_theme";
    public static final String APP_PREFERENCES = "Settings";
    public static final String APP_PREFERENCES_INNER_BROWSER = "APP_PREFERENCES_INNER_BROWSER";
    RecyclerView recyclerView;
    PostAdapter adapter;
    PostRepository.PostDiffUtilCallback diffUtilCallback;
    PostViewModel model;
    Button favButon;
    public static String yes_THEME;
    SharedPreferences sPref;
    public SharedPreferences mSettings;
    Toolbar toolbar;
    private boolean isInInnerBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadText();
        setContentView(R.layout.activity_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        final SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.srl_container);

        if (!VKSdk.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
        } else {
            recyclerView = findViewById(R.id.recyclerView);

            adapter = new PostAdapter(diffUtilCallback, this);
            model= new ViewModelProvider(this).get(PostViewModel.class);

            model.getAllPosts(MainActivity.this).observe(MainActivity.this, new Observer<PagedList<Post>>() {
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
        favButon = findViewById(R.id.favoriteFeedMAIN);
        favButon.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });
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

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.submenu1:
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                yes_THEME = "YES";
                saveText();
            }
            return true;
            case R.id.submenu2:
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                yes_THEME = "NO";
                saveText();

            }
            return true;
            case R.id.action_inner_browser:
            {
                item.setChecked(!item.isChecked());
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putBoolean(APP_PREFERENCES_INNER_BROWSER, item.isChecked());
                editor.apply();
            }
            return true;
            case R.id.authorization:
            {
                Intent intent = new Intent(MainActivity.this, AuthorizationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            return true;
            case R.id.exit:
            {
                VKSdk.logout();
                model.cleanDB();
                Intent intent = new Intent(MainActivity.this, AuthorizationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(THEME, yes_THEME );
        super.onSaveInstanceState(outState);
    }

    public void saveText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(THEME,yes_THEME);
        ed.apply();
    }

    public void loadText() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(THEME, null);

        if (savedText!=null &&savedText.equals("YES") ) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveText();
    }
}
