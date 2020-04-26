package com.example.feedler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.feedler.PagedList.PostAdapter;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity /*implements PostAdapter.Listener*/ {
    RecyclerView recyclerView;
    PostAdapter adapter;
    PostRepository.PostDiffUtilCallback diffUtilCallback;

    private ArrayList<Post> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (!VKSdk.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
        } else {
            recyclerView = findViewById(R.id.recyclerView);

            PostViewModel model = new ViewModelProvider(this).get(PostViewModel.class);

            adapter = new PostAdapter(diffUtilCallback, this);

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

//    @Override
//    public void insertFavorite(Post post) {
//        post.id=model.getFavoritePost().get(model.getFavoritePost().size()-1).id+1;
//        model.insertFavorite(post);
//        Log.e("insertFavorite", " "+post.id+" "+ post.getDate());
//    }
//
//    @Override
//    public void deleteFavorite(Post post) {
//        List<Post> favoritePost = model.getFavoritePost();
//        for (int i =0 ; i<favoritePost.size(); i++){
//            if (favoritePost.get(i).getDate().equals(post.getDate())){
//                Log.e( "deleteFavorite" , "before size=" + model.getFavoritePost().size()+ "last id"+model.getFavoritePost().get(model.getFavoritePost().size()-1).id);
//                model .deleteFavorite(favoritePost.get(i));
//                Log.e( "deleteFavorite" , "after size=" + model.getFavoritePost().size()+ "last id"+model.getFavoritePost().get(model.getFavoritePost().size()-1).id);
//            }
//        }
//    }


}
