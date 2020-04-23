package com.example.feedler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.feedler.PagedList.MyPositionalDataSource;
import com.example.feedler.PagedList.PostAdapter;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PostAdapter adapter;
    PostDiffUtilCallback diffUtilCallback;


    private ArrayList<Post> arrayList = new ArrayList<>();
    private PostViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (!VKSdk.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
        }

        final SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.srl_container);

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

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                model = ViewModelProviders.of(MainActivity.this).get(PostViewModel.class);

                adapter = new PostAdapter(diffUtilCallback);

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
class PostDiffUtilCallback extends DiffUtil.ItemCallback<Post> {

    public PostDiffUtilCallback() {
    }

    @Override
    public boolean areItemsTheSame(Post oldPost, Post newPost) {
        return oldPost.getId() == newPost.getId();
    }

    @Override
    public boolean areContentsTheSame(Post oldPost, Post newPost) {
        return oldPost.getPostText().equals(newPost.getPostText());
    }

}